package lk.sliit.visionacademy.sports_academy_management.controller;

import jakarta.validation.Valid;
import lk.sliit.visionacademy.sports_academy_management.dto.FeeGenerationRequest;
import lk.sliit.visionacademy.sports_academy_management.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.sports_academy_management.dto.PaymentResponseDTO;
import lk.sliit.visionacademy.sports_academy_management.entity.FeeStructure;
import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import lk.sliit.visionacademy.sports_academy_management.entity.PaymentStatus;
import lk.sliit.visionacademy.sports_academy_management.entity.ReminderChannel;
import lk.sliit.visionacademy.sports_academy_management.exception.FeeTierNotFoundException;
import lk.sliit.visionacademy.sports_academy_management.exception.InsufficientPaymentAmountException;
import lk.sliit.visionacademy.sports_academy_management.exception.InvalidPaymentStateException;
import lk.sliit.visionacademy.sports_academy_management.exception.PaymentProcessingException;
import lk.sliit.visionacademy.sports_academy_management.repository.PlayerRepository;
import lk.sliit.visionacademy.sports_academy_management.service.IPaymentService;
import lk.sliit.visionacademy.sports_academy_management.service.PaymentReceiptPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Boundary Class for Billing and Payments.
 * Exposes REST API endpoints and web UI controller methods for parents, players, and administrators.
 * Fulfills Single Responsibility Principle for payment viewing, processing, fee generation,
 * receipt generation, and overdue reminder dispatching.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    // Dependency injection via the Service Interface (Decoupled & Encapsulated)
    @Autowired
    private IPaymentService paymentService;

    // OOAD DEPENDENCY (Lecture 04/06.1): Service for formatting receipts without storing payment state
    @Autowired
    private PaymentReceiptPrinter paymentReceiptPrinter;

    // Integration with Member 1 Player CRUD
    @Autowired(required = false)
    private PlayerRepository playerRepository;

    // =========================================================================
    // UI ENDPOINTS (Thymeleaf Views)
    // =========================================================================

    /**
     * Web page for parents and players to view payment records.
     * URL: GET /payments/view or GET /payments/view?playerId=1024
     */
    @GetMapping("/view")
    public String viewPayments(@RequestParam(required = false) Integer playerId,
                               @RequestParam(required = false) String error,
                               Model model) {
        try {
            // Trigger overdue updates before display
            paymentService.calculateOverduePayments();

            List<Payment> payments;
            if (playerId != null) {
                payments = paymentService.getPaymentsByPlayer(playerId);
            } else {
                payments = paymentService.getPayments();
            }
            model.addAttribute("payments", payments);
            model.addAttribute("selectedPlayerId", playerId);
            if (error != null) {
                model.addAttribute("error", error);
            }

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching payments: " + e.getMessage());
        } finally {
            System.out.println("Payment view request processed for playerId: " + playerId);
        }

        return "view-payments";
    }

    /**
     * Web page displaying official payment receipt and statement.
     * URL: GET /payments/receipt/{id}
     */
    @GetMapping("/receipt/{id}")
    public String viewReceipt(@PathVariable("id") Integer id, Model model) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            model.addAttribute("payment", payment);
            // OOAD Dependency: Passes Payment temporarily to receipt printer (Lecture 04/06.1)
            model.addAttribute("receiptText", paymentReceiptPrinter.printReceipt(payment));
            return "payment-receipt";
        } catch (Exception e) {
            return "redirect:/payments/view?error=" + e.getMessage();
        }
    }

    /**
     * Plain-text printable receipt endpoint.
     * Demonstrates OOAD Dependency Relationship: PaymentReceiptPrinter takes Payment as method argument.
     * URL: GET /payments/receipt/{id}/raw
     */
    @GetMapping(value = "/receipt/{id}/raw", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> printRawReceipt(@PathVariable("id") Integer id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            String rawReceipt = paymentReceiptPrinter.printReceipt(payment);
            return ResponseEntity.ok(rawReceipt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    /**
     * Web action to process an outstanding fee payment and redirect directly to receipt.
     * URL: POST /payments/pay/{id}
     */
    @PostMapping("/pay/{id}")
    public String processPaymentDirect(@PathVariable("id") Integer id,
                                       @RequestParam(defaultValue = "ONLINE") String paymentMethod) {
        try {
            paymentService.processPayment(id);
            return "redirect:/payments/receipt/" + id;
        } catch (Exception e) {
            return "redirect:/payments/view?error=" + e.getMessage();
        }
    }

    /**
     * Web form for Administrator to generate and assign fees to players.
     * URL: GET /payments/generate
     */
    @GetMapping("/generate")
    public String showGenerateFeeForm(@RequestParam(required = false) Integer playerId, Model model) {
        try {
            List<FeeStructure> feeStructures = paymentService.getAllFeeStructures();
            model.addAttribute("feeStructures", feeStructures);
            FeeGenerationRequest request = new FeeGenerationRequest();
            if (playerId != null) {
                request.setPlayerId(playerId);
            }
            request.setDueDate(LocalDate.now().plusDays(7));
            model.addAttribute("feeRequest", request);
            if (playerRepository != null) {
                model.addAttribute("players", playerRepository.findAll());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading fee tiers: " + e.getMessage());
        }
        return "generate-fee";
    }

    /**
     * Web action to process fee generation submission.
     * URL: POST /payments/generate
     */
    @PostMapping("/generate")
    public String generateFee(@Valid @ModelAttribute("feeRequest") FeeGenerationRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("feeStructures", paymentService.getAllFeeStructures());
            if (playerRepository != null) {
                model.addAttribute("players", playerRepository.findAll());
            }
            return "generate-fee";
        }

        try {
            LocalDate dueDate = (request.getDueDate() != null) ? request.getDueDate() : LocalDate.now().plusDays(7);
            paymentService.generateMonthlyFee(request.getPlayerId(), request.getFeeId(), dueDate);
            redirectAttributes.addFlashAttribute("success", "Fee successfully generated and assigned to Player #" + request.getPlayerId());
            return "redirect:/payments/view";
        } catch (FeeTierNotFoundException e) {
            // MULTIPLE CATCH (Lecture 10/11 Slides 30-31): Specific domain configuration catch
            model.addAttribute("feeStructures", paymentService.getAllFeeStructures());
            model.addAttribute("error", "Fee Configuration Error: " + e.getMessage());
            return "generate-fee";
        } catch (PaymentProcessingException e) {
            // General domain business catch
            model.addAttribute("feeStructures", paymentService.getAllFeeStructures());
            model.addAttribute("error", e.getMessage());
            return "generate-fee";
        } catch (Exception e) {
            // Fallback catch for unexpected errors
            model.addAttribute("feeStructures", paymentService.getAllFeeStructures());
            model.addAttribute("error", "An unexpected system error occurred: " + e.getMessage());
            return "generate-fee";
        } finally {
            // FINALLY BLOCK (Lecture 10/11 Slides 36-40): Always executed for audit tracing
            System.out.println("[AUDIT FINALLY] generateFee request execution completed for Player #" + request.getPlayerId());
        }
    }

    /**
     * Web action for Admin to batch-trigger overdue payment reminders.
     * URL: POST /payments/reminders/trigger
     */
    @PostMapping("/reminders/trigger")
    public String triggerOverdueReminders(RedirectAttributes redirectAttributes) {
        try {
            int count = paymentService.sendOverdueReminders();
            redirectAttributes.addFlashAttribute("success", "Successfully triggered " + count + " overdue reminder(s) via SMS notification.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error triggering reminders: " + e.getMessage());
        }
        return "redirect:/payments/view";
    }

    /**
     * Web action to dispatch an immediate reminder for a specific overdue payment.
     * URL: POST /payments/reminders/send/{paymentId}
     */
    @PostMapping("/reminders/send/{paymentId}")
    public String sendIndividualReminder(@PathVariable Integer paymentId,
                                         @RequestParam(defaultValue = "SMS") ReminderChannel channel,
                                         RedirectAttributes redirectAttributes) {
        try {
            paymentService.sendReminder(paymentId, channel);
            redirectAttributes.addFlashAttribute("success", "Reminder dispatched via " + channel + " for Payment #" + paymentId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to dispatch reminder: " + e.getMessage());
        }
        return "redirect:/payments/view";
    }

    // =========================================================================
    // REST API ENDPOINTS (JSON)
    // =========================================================================

    /**
     * REST Endpoint: Fetch payment history.
     * Optionally filter by playerId.
     * URL: GET /payments/api or GET /payments/api?playerId=1024
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> getPaymentHistory(@RequestParam(required = false) Integer playerId) {
        try {
            paymentService.calculateOverduePayments();

            List<Payment> payments = (playerId != null)
                    ? paymentService.getPaymentsByPlayer(playerId)
                    : paymentService.getPayments();

            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving payment history: " + e.getMessage());
        }
    }

    /**
     * REST Endpoint: Query payments by current status (PENDING, PAID, OVERDUE).
     * URL: GET /payments/api/status/{status}
     */
    @GetMapping("/api/status/{status}")
    @ResponseBody
    public ResponseEntity<?> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        try {
            paymentService.calculateOverduePayments();
            List<Payment> payments = paymentService.getPayments(status);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving payments by status: " + e.getMessage());
        }
    }

    /**
     * REST Endpoint: Process payment with validated DTO payload.
     * URL: POST /payments/api/pay
     */
    @PostMapping("/api/pay")
    @ResponseBody
    public ResponseEntity<?> processPaymentApi(@Valid @RequestBody PaymentProcessRequest request) {
        try {
            Payment payment = paymentService.processPayment(request);
            return ResponseEntity.ok(PaymentResponseDTO.fromEntity(payment));
        } catch (InvalidPaymentStateException | InsufficientPaymentAmountException e) {
            // MULTI-CATCH CLAUSE (Lecture 10/11 Slide 32):
            // Catches more than one type of exception with a single handler. Parameter 'e' is implicitly final.
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        } catch (PaymentProcessingException e) {
            // MULTIPLE CATCH (Lecture 10/11 Slides 30-31): General domain checked exception handler
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error processing payment: " + e.getMessage());
        }
    }

    /**
     * REST Endpoint: Generate fee for a player.
     * URL: POST /payments/api/generate
     */
    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<?> generateFeeApi(@Valid @RequestBody FeeGenerationRequest request) {
        try {
            LocalDate dueDate = (request.getDueDate() != null) ? request.getDueDate() : LocalDate.now().plusDays(7);
            Payment payment = paymentService.generateMonthlyFee(request.getPlayerId(), request.getFeeId(), dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponseDTO.fromEntity(payment));
        } catch (FeeTierNotFoundException e) {
            // MULTIPLE CATCH (Lecture 10/11 Slides 30-31): Specific checked exception returns 404 NOT FOUND
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (PaymentProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error generating fee: " + e.getMessage());
        }
    }

    /**
     * REST Endpoint: Trigger overdue reminders batch.
     * URL: POST /payments/api/reminders/trigger
     */
    @PostMapping("/api/reminders/trigger")
    @ResponseBody
    public ResponseEntity<?> triggerOverdueRemindersApi() {
        try {
            int count = paymentService.sendOverdueReminders();
            return ResponseEntity.ok(Map.of("message", "Reminders dispatched successfully", "dispatchedCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error triggering reminders: " + e.getMessage());
        }
    }

    /**
     * REST Endpoint: Retrieve reminder logs for a specific payment.
     * URL: GET /payments/api/reminders/{paymentId}
     */
    @GetMapping("/api/reminders/{paymentId}")
    @ResponseBody
    public ResponseEntity<?> getReminderLogsApi(@PathVariable Integer paymentId) {
        try {
            return ResponseEntity.ok(paymentService.getReminderLogs(paymentId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving reminder logs: " + e.getMessage());
        }
    }
}


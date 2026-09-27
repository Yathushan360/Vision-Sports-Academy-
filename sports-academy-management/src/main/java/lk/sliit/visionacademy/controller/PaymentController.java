package lk.sliit.visionacademy.controller;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Boundary Class for Billing and Payments.
 * Exposes REST API endpoints and web UI controller methods for parents and players.
 */
@Controller
@RequestMapping("/payments")
public class PaymentController {

    // Dependency injection via the Service Interface (Decoupled & Encapsulated)
    @Autowired
    private IPaymentService paymentService;

    // =========================================================================
    // UI ENDPOINTS (Thymeleaf View)
    // =========================================================================

    /**
     * Web page for parents and players to view payment records.
     * URL: GET /payments/view or GET /payments/view?playerId=1024
     */
    @GetMapping("/view")
    public String viewPayments(@RequestParam(required = false) Integer playerId, Model model) {
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

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching payments: " + e.getMessage());
        } finally {
            System.out.println("Payment view request processed for playerId: " + playerId);
        }

        return "view-payments";
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
}

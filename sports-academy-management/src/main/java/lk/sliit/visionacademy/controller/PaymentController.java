package lk.sliit.visionacademy.controller;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    
    // Dependency injection via the Interface (OOAD Concept)
    @Autowired
    private IPaymentService paymentService;

    @GetMapping("/view")
    public String viewPayments(@RequestParam(required = false) Integer playerId, Model model) {
        try {
            // Trigger overdue check
            paymentService.calculateOverduePayments();
            
            List<Payment> payments;
            if (playerId != null) {
                payments = paymentService.getPaymentsByPlayer(playerId);
            } else {
                payments = paymentService.getPayments();
            }
            model.addAttribute("payments", payments);
            
        } catch (Exception e) {
            // CATCH: Handle unexpected runtime errors (OOAD Concept)
            model.addAttribute("error", "An error occurred while fetching payments: " + e.getMessage());
        } finally {
            // FINALLY: Always executes (OOAD Concept)
            System.out.println("Payment view request processed.");
        }
        
        return "view-payments"; 
    }
}

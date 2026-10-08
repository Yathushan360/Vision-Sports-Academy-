package lk.sliit.visionacademy.scheduler;

import lk.sliit.visionacademy.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Control Component for automated batch processing.
 * Runs daily at midnight to assess overdue fee payments, apply late fee penalties,
 * and dispatch overdue reminder notices without requiring user intervention.
 * Enforces Automated Control Processing (OOAD Pattern).
 */
@Component
public class PaymentOverdueScheduler {

    @Autowired
    private IPaymentService paymentService;

   
    }
}

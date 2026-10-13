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

    /**
     * Executes automatically every day at midnight (00:00:00).
     * Cron expression: "0 0 0 * * ?"
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyOverdueAssessment() {
        System.out.println("[SCHEDULER] Starting automated daily overdue assessment job...");
        try {
            // 1. Calculate and update overdue statuses and late fee penalties
            paymentService.calculateOverduePayments();

            // 2. Dispatch automated reminder notifications to parents/players
            int remindersSent = paymentService.sendOverdueReminders();

            System.out.println("[SCHEDULER] Daily overdue job finished successfully. Reminders dispatched: " + remindersSent);
        } catch (Exception e) {
            System.err.println("[SCHEDULER] Error during automated overdue execution: " + e.getMessage());
        }
    }
}

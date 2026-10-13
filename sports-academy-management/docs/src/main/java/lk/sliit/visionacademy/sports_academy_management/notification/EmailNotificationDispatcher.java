package lk.sliit.visionacademy.sports_academy_management.notification;

import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import org.springframework.stereotype.Component;

/**
 * Concrete Subclass: Specializes AbstractNotificationDispatcher for Email channels.
 * Demonstrates:
 * 1. Specialization of abstract superclass.
 * 2. Constructor chaining via super("EMAIL", 5).
 * 3. Runtime Polymorphism via overridden sendPayload.
 */
@Component("emailNotificationDispatcher")
public class EmailNotificationDispatcher extends AbstractNotificationDispatcher {

    public EmailNotificationDispatcher() {
        super("EMAIL", 5); // Constructor chaining to superclass
    }

    @Override
    protected boolean sendPayload(Payment payment, String message) {
        // Simulates SMTP email client transmission
        System.out.println("[SMTP EMAIL] Sending Email to parent of Player #" + payment.getPlayerId()
                + " | Subject: Vision Sports Academy Fee Reminder | Message: \"" + message + "\"");
        return true;
    }
}


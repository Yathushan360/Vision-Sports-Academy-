package lk.sliit.visionacademy.sports_academy_management.notification;

import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import org.springframework.stereotype.Component;

/**
 * Concrete Subclass: Specializes AbstractNotificationDispatcher for SMS channels.
 * Demonstrates:
 * 1. Inheritance (extends AbstractNotificationDispatcher).
 * 2. Constructor chaining via super("SMS", 3).
 * 3. Method Overriding (@Override) of abstract method sendPayload.
 */
@Component("smsNotificationDispatcher")
public class SmsNotificationDispatcher extends AbstractNotificationDispatcher {

    public SmsNotificationDispatcher() {
        super("SMS", 3); // Constructor chaining to superclass
    }

    @Override
    protected boolean sendPayload(Payment payment, String message) {
        // Simulates SMS Gateway gateway payload transmission
        System.out.println("[SMS GATEWAY] Sending SMS to Player #" + payment.getPlayerId()
                + " | Text: \"" + message + "\" | Balance Due: Rs. " + payment.getAmount());
        return true;
    }
}


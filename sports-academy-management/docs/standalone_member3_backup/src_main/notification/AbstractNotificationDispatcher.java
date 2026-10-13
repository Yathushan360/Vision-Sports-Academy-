package lk.sliit.visionacademy.notification;

import lk.sliit.visionacademy.entity.Payment;

/**
 * Abstract Superclass for notification dispatchers.
 * Demonstrates Core OOAD Concepts from Lecture 05/10:
 * 1. Abstract Class: Models common but incomplete behavior; cannot be instantiated directly.
 * 2. Encapsulation: Protected attributes inherited by subclasses.
 * 3. Constructor Chaining: Parameterized constructor invoked via super().
 * 4. Template Method Pattern: Defines the invariant dispatch workflow and delegates primitive steps to abstract methods.
 */
public abstract class AbstractNotificationDispatcher {

    protected final String channelName;
    protected final int retryLimit;

    /**
     * Parameterized Constructor demonstrating Constructor Chaining in child classes.
     */
    protected AbstractNotificationDispatcher(String channelName, int retryLimit) {
        this.channelName = channelName;
        this.retryLimit = retryLimit;
    }

    /**
     * Template Method (Concrete method with implementation):
     * Orchestrates the standard notification sequence.
     */
    public final boolean dispatch(Payment payment, String message) {
        if (payment == null) {
            System.err.println("[" + channelName + "] Cannot dispatch notification: Payment object is null.");
            return false;
        }

        // Common pre-dispatch step
        logPreDispatch(payment);

        // Primitive step: Abstract method implemented by specific subclasses
        boolean success = sendPayload(payment, message);

        // Common post-dispatch audit step
        if (success) {
            System.out.println("[" + channelName + "] Notification successfully delivered to Player #" + payment.getPlayerId());
        } else {
            System.err.println("[" + channelName + "] Notification delivery failed for Player #" + payment.getPlayerId());
        }

        return success;
    }

    /**
     * Abstract Method (No implementation):
     * MUST be implemented by all concrete subclasses (Slide 9 & 10).
     */
    protected abstract boolean sendPayload(Payment payment, String message);

    /**
     * Concrete helper method shared across all child classes.
     */
    protected void logPreDispatch(Payment payment) {
        System.out.println("[" + channelName + "] Preparing to dispatch reminder for Payment #"
                + payment.getPaymentId() + " (Amount: Rs. " + payment.getAmount() + ")");
    }

    public String getChannelName() {
        return channelName;
    }

    public int getRetryLimit() {
        return retryLimit;
    }
}

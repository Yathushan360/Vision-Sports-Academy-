package lk.sliit.visionacademy.exception;

/**
 * Custom checked exception thrown when an invalid payment state transition is attempted
 * (e.g., attempting to process a payment that is already marked as PAID).
 * Demonstrates OOP Inheritance and Exception Handling.
 */
public class InvalidPaymentStateException extends PaymentProcessingException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }
}

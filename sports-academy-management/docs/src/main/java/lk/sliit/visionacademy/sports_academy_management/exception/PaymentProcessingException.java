package lk.sliit.visionacademy.sports_academy_management.exception;

/**
 * Checked Exception thrown when a payment processing or transaction workflow fails.
 * Extends AcademyBillingException, demonstrating multi-level exception inheritance.
 */
public class PaymentProcessingException extends AcademyBillingException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}


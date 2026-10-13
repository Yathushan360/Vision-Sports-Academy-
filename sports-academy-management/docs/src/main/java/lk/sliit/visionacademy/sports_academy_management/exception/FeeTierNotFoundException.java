package lk.sliit.visionacademy.sports_academy_management.exception;

/**
 * Specialized Checked Exception thrown when a requested fee tier/structure ID does not exist.
 * Subclass of PaymentProcessingException (which inherits from AcademyBillingException).
 * Demonstrates 3-tier Exception Hierarchy: Throwable -> Exception -> AcademyBillingException -> PaymentProcessingException -> FeeTierNotFoundException.
 */
public class FeeTierNotFoundException extends PaymentProcessingException {

    private final Integer feeId;

    public FeeTierNotFoundException(String message, Integer feeId) {
        super(message);
        this.feeId = feeId;
    }

    public Integer getFeeId() {
        return feeId;
    }
}


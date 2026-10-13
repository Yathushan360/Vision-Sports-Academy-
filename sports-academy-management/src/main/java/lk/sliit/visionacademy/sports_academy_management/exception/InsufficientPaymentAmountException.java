package lk.sliit.visionacademy.sports_academy_management.exception;

import java.math.BigDecimal;

/**
 * Specialized Checked Exception thrown when a payment amount is below the required invoice total.
 * Subclass of PaymentProcessingException.
 */
public class InsufficientPaymentAmountException extends PaymentProcessingException {

    private final BigDecimal requiredAmount;
    private final BigDecimal providedAmount;

    public InsufficientPaymentAmountException(String message, BigDecimal requiredAmount, BigDecimal providedAmount) {
        super(message);
        this.requiredAmount = requiredAmount;
        this.providedAmount = providedAmount;
    }

    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }

    public BigDecimal getProvidedAmount() {
        return providedAmount;
    }
}


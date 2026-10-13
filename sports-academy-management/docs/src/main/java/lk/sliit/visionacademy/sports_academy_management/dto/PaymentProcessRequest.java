package lk.sliit.visionacademy.sports_academy_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for processing a fee payment.
 * Enforces Encapsulation and validates incoming request data before reaching the domain layer.
 */
public class PaymentProcessRequest {

    @NotNull(message = "Payment ID is required")
    @Min(value = 1, message = "Payment ID must be a positive number")
    private Integer paymentId;

    @NotNull(message = "Paid amount is required")
    @Positive(message = "Paid amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required (e.g., CASH, CARD, BANK_TRANSFER, ONLINE)")
    private String paymentMethod;

    @NotNull(message = "Paid date is required")
    private LocalDate paidDate;

    private String transactionReference;

    private String remarks;

    public PaymentProcessRequest() {
    }

    public PaymentProcessRequest(Integer paymentId, BigDecimal amount, String paymentMethod, LocalDate paidDate, String transactionReference, String remarks) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paidDate = paidDate;
        this.transactionReference = transactionReference;
        this.remarks = remarks;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}


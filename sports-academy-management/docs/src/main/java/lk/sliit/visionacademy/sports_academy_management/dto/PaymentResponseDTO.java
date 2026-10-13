package lk.sliit.visionacademy.sports_academy_management.dto;

import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import lk.sliit.visionacademy.sports_academy_management.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object representing outgoing payment details.
 * Encapsulates internal entity state and decouples database schema from UI / API consumers.
 */
public class PaymentResponseDTO {

    private Integer paymentId;
    private Integer playerId;
    private String feeType;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private PaymentStatus status;
    private boolean isOverdue;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(Integer paymentId, Integer playerId, String feeType, BigDecimal amount, LocalDate dueDate, LocalDate paidDate, PaymentStatus status, boolean isOverdue) {
        this.paymentId = paymentId;
        this.playerId = playerId;
        this.feeType = feeType;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.status = status;
        this.isOverdue = isOverdue;
    }

    /**
     * Factory method to safely transform a domain Payment entity into a PaymentResponseDTO.
     * Enforces Information Hiding by keeping entity internals contained.
     */
    public static PaymentResponseDTO fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        String feeTypeName = (payment.getFeeStructure() != null && payment.getFeeStructure().getFeeType() != null)
                ? payment.getFeeStructure().getFeeType().name()
                : "GENERAL_TRAINING";

        boolean overdue = payment.getStatus() == PaymentStatus.OVERDUE
                || (payment.getStatus() == PaymentStatus.PENDING && payment.getDueDate() != null && payment.getDueDate().isBefore(LocalDate.now()));

        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getPlayerId(),
                feeTypeName,
                payment.getAmount(),
                payment.getDueDate(),
                payment.getPaidDate(),
                payment.getStatus(),
                overdue
        );
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }
}


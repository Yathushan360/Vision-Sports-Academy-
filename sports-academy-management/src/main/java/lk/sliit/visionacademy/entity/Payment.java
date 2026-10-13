package lk.sliit.visionacademy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "player_id", nullable = false)
    private Integer playerId;

    /**
     * OOAD CONCEPT: AGGREGATION RELATIONSHIP (Lecture 04 Slides 8-14, Lecture 06.1):
     * - Weak "Has-A" relationship (represented by an open/hollow diamond on FeeStructure in UML).
     * - FeeStructure exists independently in the academy catalog.
     * - If individual Payment records are settled or removed, the parent FeeStructure continues
     *   to exist without being affected (shared independent lifecycle).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private FeeStructure feeStructure;

    /**
     * OOAD CONCEPT: COMPOSITION RELATIONSHIP (Lecture 04 Slides 15-28, Lecture 06.1):
     * - Strong "Whole-Part" relationship (represented by a filled diamond on Payment in UML).
     * - Lifetime Dependency: PaymentAuditTrail parts cannot exist without the Payment whole.
     * - Enforced via CascadeType.ALL and orphanRemoval = true: destroying a Payment
     *   automatically destroys all its associated audit log parts.
     */
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaymentAuditTrail> auditTrail = new ArrayList<>();

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Payment() {
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

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PaymentAuditTrail> getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(List<PaymentAuditTrail> auditTrail) {
        this.auditTrail = auditTrail;
    }

    /**
     * Composition helper: Instantiates and attaches a PaymentAuditTrail part to this Payment whole.
     */
    public void addAuditTrail(String action, String details) {
        if (this.auditTrail == null) {
            this.auditTrail = new ArrayList<>();
        }
        PaymentAuditTrail trail = new PaymentAuditTrail(this, action, details);
        this.auditTrail.add(trail);
    }
}

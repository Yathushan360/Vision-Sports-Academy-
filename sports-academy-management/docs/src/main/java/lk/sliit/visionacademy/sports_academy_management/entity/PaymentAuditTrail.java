package lk.sliit.visionacademy.sports_academy_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an immutable audit log entry for financial events on a Payment.
 *
 * OOAD CONCEPT: COMPOSITION RELATIONSHIP (Lecture 04 Slides 15-28, Lecture 06.1):
 * - Strong "Whole-Part" relationship (represented by a filled diamond on Payment in UML).
 * - Lifetime Dependency: PaymentAuditTrail has no standalone identity or meaning
 *   outside the context of its parent Payment.
 * - When the parent Payment entity is deleted, all associated PaymentAuditTrail parts
 *   are cascade-destroyed (CascadeType.ALL, orphanRemoval = true).
 */
@Entity
@Table(name = "payment_audit_trails")
public class PaymentAuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_id")
    private Long trailId;

    /**
     * Parent Whole: Payment entity that owns this audit trail part.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnore
    private Payment payment;

    @Column(name = "action", nullable = false, length = 60)
    private String action;

    @Column(name = "details", length = 255)
    private String details;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public PaymentAuditTrail() {
        this.recordedAt = LocalDateTime.now();
    }

    public PaymentAuditTrail(Payment payment, String action, String details) {
        this.payment = payment;
        this.action = action;
        this.details = details;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getTrailId() {
        return trailId;
    }

    public void setTrailId(Long trailId) {
        this.trailId = trailId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}


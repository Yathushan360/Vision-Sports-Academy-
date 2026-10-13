package lk.sliit.visionacademy.sports_academy_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing academy fee rates and tiers.
 *
 * OOAD CONCEPT: AGGREGATION RELATIONSHIP (Lecture 04 Slides 8-14, Lecture 06.1):
 * - FeeStructure serves as the independent catalog configuration ("Whole" in catalog context).
 * - Payments hold an aggregation reference to FeeStructure (Payment "*" -- "1" FeeStructure).
 * - Weak lifecycle coupling: FeeStructure exists independently of whether any Payments
 *   have been generated, settled, or deleted.
 */
@Entity
@Table(name = "fee_structure")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Integer feeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "age_group", length = 10)
    private String ageGroup;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    public FeeStructure() {
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}


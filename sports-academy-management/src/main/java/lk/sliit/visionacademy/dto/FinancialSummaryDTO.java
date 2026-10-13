package lk.sliit.visionacademy.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Data Transfer Object encapsulating aggregate financial metrics and analytics.
 * Enforces Encapsulation and Information Hiding.
 * Fulfills the summarization role of the Report domain entity.
 */
public class FinancialSummaryDTO {

    private BigDecimal totalRevenue;
    private BigDecimal totalOverdueAmount;
    private BigDecimal totalPendingAmount;
    private BigDecimal totalInvoicedAmount;

    private int paidCount;
    private int pendingCount;
    private int overdueCount;
    private int totalTransactionsCount;

    // Java Collections (Lecture 07): HashSet deduplication & TreeSet sorted distinct counts
    private int uniqueDebtorsCount;
    private int distinctDueDatesCount;

    private double collectionRate;
    private LocalDateTime generatedDate;

    public FinancialSummaryDTO() {
        this.totalRevenue = BigDecimal.ZERO;
        this.totalOverdueAmount = BigDecimal.ZERO;
        this.totalPendingAmount = BigDecimal.ZERO;
        this.totalInvoicedAmount = BigDecimal.ZERO;
        this.generatedDate = LocalDateTime.now();
    }

    public FinancialSummaryDTO(BigDecimal totalRevenue,
                               BigDecimal totalOverdueAmount,
                               BigDecimal totalPendingAmount,
                               BigDecimal totalInvoicedAmount,
                               int paidCount,
                               int pendingCount,
                               int overdueCount,
                               int totalTransactionsCount,
                               double collectionRate,
                               LocalDateTime generatedDate) {
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalOverdueAmount = totalOverdueAmount != null ? totalOverdueAmount : BigDecimal.ZERO;
        this.totalPendingAmount = totalPendingAmount != null ? totalPendingAmount : BigDecimal.ZERO;
        this.totalInvoicedAmount = totalInvoicedAmount != null ? totalInvoicedAmount : BigDecimal.ZERO;
        this.paidCount = paidCount;
        this.pendingCount = pendingCount;
        this.overdueCount = overdueCount;
        this.totalTransactionsCount = totalTransactionsCount;
        this.collectionRate = collectionRate;
        this.generatedDate = generatedDate != null ? generatedDate : LocalDateTime.now();
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }

    public void setTotalOverdueAmount(BigDecimal totalOverdueAmount) {
        this.totalOverdueAmount = totalOverdueAmount;
    }

    public BigDecimal getTotalPendingAmount() {
        return totalPendingAmount;
    }

    public void setTotalPendingAmount(BigDecimal totalPendingAmount) {
        this.totalPendingAmount = totalPendingAmount;
    }

    public BigDecimal getTotalInvoicedAmount() {
        return totalInvoicedAmount;
    }

    public void setTotalInvoicedAmount(BigDecimal totalInvoicedAmount) {
        this.totalInvoicedAmount = totalInvoicedAmount;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public void setPaidCount(int paidCount) {
        this.paidCount = paidCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public int getTotalTransactionsCount() {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount(int totalTransactionsCount) {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public double getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(double collectionRate) {
        this.collectionRate = collectionRate;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public BigDecimal getFormattedCollectionRate() {
        return BigDecimal.valueOf(collectionRate).setScale(1, RoundingMode.HALF_UP);
    }

    public int getUniqueDebtorsCount() {
        return uniqueDebtorsCount;
    }

    public void setUniqueDebtorsCount(int uniqueDebtorsCount) {
        this.uniqueDebtorsCount = uniqueDebtorsCount;
    }

    public int getDistinctDueDatesCount() {
        return distinctDueDatesCount;
    }

    public void setDistinctDueDatesCount(int distinctDueDatesCount) {
        this.distinctDueDatesCount = distinctDueDatesCount;
    }
}

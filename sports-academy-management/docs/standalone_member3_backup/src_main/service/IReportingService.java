package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service Contract for Financial Reporting & Analytics.
 * Fulfills the Report component from the OOAD Domain Model.
 * Applies the Information Expert pattern to aggregate transaction records into business insights.
 */
public interface IReportingService {

    /**
     * Aggregates total revenue, pending balances, overdue debts, and collection efficiency.
     */
    FinancialSummaryDTO getFinancialSummary();

    /**
     * Generates a comprehensive list of all outstanding fee payments (PENDING and OVERDUE).
     * Directly fulfills generateOutstandingReport() from the class diagram.
     */
    List<Payment> generateOutstandingReport();

    /**
     * Calculates revenue breakdown grouped by fee structure type (e.g. MONTHLY_TRAINING, REGISTRATION).
     * Utilizes LinkedHashMap to maintain predictable presentation ordering (Lecture 07).
     */
    Map<String, BigDecimal> getRevenueByFeeType();

    /**
     * Calculates the breakdown count of payments by their status.
     */
    Map<String, Long> getStatusDistribution();

    /**
     * Serializes outstanding (PENDING and OVERDUE) payments into CSV bytes for file download.
     * Fulfills Open-Closed Principle and Data Serialization requirements.
     */
    byte[] generateOutstandingReportCsv();

    /**
     * Java Collection Framework: SortedSet & TreeSet (Lecture 07 Slides 20-24).
     * Extracts all distinct payment due dates in chronological sorted order.
     */
    TreeSet<LocalDate> getDistinctDueDatesSorted();

    /**
     * Java Collection Framework: Set & HashSet (Lecture 07 Slides 19-21).
     * Deduplicates outstanding debtors into a Set of unique academy player IDs.
     */
    Set<Integer> getUniqueDebtorPlayerIds();

    /**
     * O(1) membership check using HashSet to determine if a player has unpaid fees.
     */
    boolean isPlayerInArrears(Integer playerId);

    /**
     * Java Collection Framework: Map & LinkedHashMap (Lecture 07 Slides 33-38).
     * Calculates aging schedule brackets (1-7 days, 8-30 days, 31+ days)
     * maintaining strict chronological insertion order.
     */
    Map<String, BigDecimal> getAgingSchedule();
}

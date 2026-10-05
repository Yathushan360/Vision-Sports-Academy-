package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     */
    Map<String, BigDecimal> getRevenueByFeeType();

    /**
     * Calculates the breakdown count of payments by their status.
     */
    Map<String, Long> getStatusDistribution();
}

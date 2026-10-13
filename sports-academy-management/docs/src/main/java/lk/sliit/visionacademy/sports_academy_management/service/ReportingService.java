package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import lk.sliit.visionacademy.sports_academy_management.entity.PaymentStatus;
import lk.sliit.visionacademy.sports_academy_management.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Control Class for Financial Reporting & Analytics.
 * Enforces the Information Expert Pattern (GRASP) and Aggregate Calculations.
 * Fulfills the domain requirements of the Report class.
 */
@Service
public class ReportingService implements IReportingService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private IPaymentService paymentService;

    @Override
    public FinancialSummaryDTO getFinancialSummary() {
        // Ensure overdue calculations and penalties are up-to-date
        paymentService.calculateOverduePayments();

        List<Payment> payments = paymentRepository.findAll();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalOverdueAmount = BigDecimal.ZERO;
        BigDecimal totalPendingAmount = BigDecimal.ZERO;

        int paidCount = 0;
        int pendingCount = 0;
        int overdueCount = 0;

        for (Payment payment : payments) {
            BigDecimal amt = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
            if (payment.getStatus() == PaymentStatus.PAID) {
                totalRevenue = totalRevenue.add(amt);
                paidCount++;
            } else if (payment.getStatus() == PaymentStatus.PENDING) {
                totalPendingAmount = totalPendingAmount.add(amt);
                pendingCount++;
            } else if (payment.getStatus() == PaymentStatus.OVERDUE) {
                totalOverdueAmount = totalOverdueAmount.add(amt);
                overdueCount++;
            }
        }

        BigDecimal totalInvoicedAmount = totalRevenue.add(totalPendingAmount).add(totalOverdueAmount);
        int totalCount = payments.size();
        double collectionRate = (totalCount > 0) ? ((double) paidCount / totalCount) * 100.0 : 0.0;

        FinancialSummaryDTO summary = new FinancialSummaryDTO(
                totalRevenue,
                totalOverdueAmount,
                totalPendingAmount,
                totalInvoicedAmount,
                paidCount,
                pendingCount,
                overdueCount,
                totalCount,
                collectionRate,
                LocalDateTime.now()
        );

        // Populate collection-derived intelligence (Lecture 07):
        // HashSet deduplication and TreeSet distinct sorted count
        summary.setUniqueDebtorsCount(getUniqueDebtorPlayerIds().size());
        summary.setDistinctDueDatesCount(getDistinctDueDatesSorted().size());

        return summary;
    }

    @Override
    public List<Payment> generateOutstandingReport() {
        paymentService.calculateOverduePayments();
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING || p.getStatus() == PaymentStatus.OVERDUE)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getRevenueByFeeType() {
        List<Payment> paidPayments = paymentRepository.findByStatus(PaymentStatus.PAID);
        // LinkedHashMap guarantees predictable insertion order for report views (Lecture 07 Slides 33-38)
        Map<String, BigDecimal> revenueByType = new LinkedHashMap<>();

        for (Payment payment : paidPayments) {
            String feeTypeName = (payment.getFeeStructure() != null && payment.getFeeStructure().getFeeType() != null)
                    ? payment.getFeeStructure().getFeeType().name()
                    : "GENERAL_TRAINING";

            BigDecimal currentSum = revenueByType.getOrDefault(feeTypeName, BigDecimal.ZERO);
            revenueByType.put(feeTypeName, currentSum.add(payment.getAmount()));
        }

        return revenueByType;
    }

    /**
     * Java Collection Framework: SortedSet & TreeSet (Lecture 07 Slides 20-24).
     * Extracts all distinct payment due dates in chronological order.
     * Guaranteed O(log n) performance for insertions and natural ordering by LocalDate.
     */
    @Override
    public TreeSet<LocalDate> getDistinctDueDatesSorted() {
        List<Payment> payments = paymentRepository.findAll();
        TreeSet<LocalDate> sortedDates = new TreeSet<>();
        for (Payment payment : payments) {
            if (payment.getDueDate() != null) {
                sortedDates.add(payment.getDueDate());
            }
        }
        return sortedDates;
    }

    /**
     * Java Collection Framework: Set & HashSet (Lecture 07 Slides 19-21).
     * Deduplicates outstanding debtors into a Set of unique academy player IDs.
     * Guarantees O(1) performance for membership lookups without duplicates.
     */
    @Override
    public Set<Integer> getUniqueDebtorPlayerIds() {
        List<Payment> outstanding = generateOutstandingReport();
        Set<Integer> uniqueDebtors = new HashSet<>();
        for (Payment payment : outstanding) {
            if (payment.getPlayerId() != null) {
                uniqueDebtors.add(payment.getPlayerId());
            }
        }
        return uniqueDebtors;
    }

    /**
     * Checks if a player currently has outstanding or overdue debts in O(1) time.
     */
    @Override
    public boolean isPlayerInArrears(Integer playerId) {
        if (playerId == null) {
            return false;
        }
        return getUniqueDebtorPlayerIds().contains(playerId);
    }

    /**
     * Java Collection Framework: Map & LinkedHashMap (Lecture 07 Slides 33-38).
     * Calculates aging schedule brackets (1-7 days, 8-30 days, 31+ days)
     * maintaining strict chronological insertion order.
     */
    @Override
    public Map<String, BigDecimal> getAgingSchedule() {
        paymentService.calculateOverduePayments();
        List<Payment> overduePayments = paymentRepository.findByStatus(PaymentStatus.OVERDUE);
        LocalDate today = LocalDate.now();

        BigDecimal recentOverdue = BigDecimal.ZERO;    // 1 - 7 days overdue
        BigDecimal mediumOverdue = BigDecimal.ZERO;    // 8 - 30 days overdue
        BigDecimal chronicOverdue = BigDecimal.ZERO;   // 31+ days overdue

        for (Payment payment : overduePayments) {
            BigDecimal amt = (payment.getAmount() != null) ? payment.getAmount() : BigDecimal.ZERO;
            if (payment.getDueDate() != null) {
                long daysPast = java.time.temporal.ChronoUnit.DAYS.between(payment.getDueDate(), today);
                if (daysPast <= 7) {
                    recentOverdue = recentOverdue.add(amt);
                } else if (daysPast <= 30) {
                    mediumOverdue = mediumOverdue.add(amt);
                } else {
                    chronicOverdue = chronicOverdue.add(amt);
                }
            } else {
                recentOverdue = recentOverdue.add(amt);
            }
        }

        // LinkedHashMap preserves exact insertion order for dashboard charts and tables
        Map<String, BigDecimal> agingSchedule = new LinkedHashMap<>();
        agingSchedule.put("1 - 7 Days (Grace Period)", recentOverdue);
        agingSchedule.put("8 - 30 Days (Moderate Arrears)", mediumOverdue);
        agingSchedule.put("31+ Days (Critical Arrears)", chronicOverdue);

        return agingSchedule;
    }

    @Override
    public Map<String, Long> getStatusDistribution() {
        return paymentRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));
    }

    @Override
    public byte[] generateOutstandingReportCsv() {
        List<Payment> outstandingList = generateOutstandingReport();
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("Payment ID,Player ID,Fee Category,Age Group,Due Date,Amount (LKR),Status,Transaction Ref,Billing Date\n");

        // CSV Records
        for (Payment payment : outstandingList) {
            String feeCategory = (payment.getFeeStructure() != null && payment.getFeeStructure().getFeeType() != null)
                    ? payment.getFeeStructure().getFeeType().name()
                    : "GENERAL_TRAINING";

            String ageGroup = (payment.getFeeStructure() != null && payment.getFeeStructure().getAgeGroup() != null)
                    ? payment.getFeeStructure().getAgeGroup()
                    : "All Ages";

            String dueDate = payment.getDueDate() != null ? payment.getDueDate().toString() : "N/A";
            String amount = payment.getAmount() != null ? payment.getAmount().toPlainString() : "0.00";
            String status = payment.getStatus() != null ? payment.getStatus().name() : "UNKNOWN";
            String txnRef = payment.getTransactionReference() != null ? payment.getTransactionReference() : "NONE";
            String createdAt = payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : "N/A";

            csv.append(payment.getPaymentId()).append(",")
               .append(payment.getPlayerId()).append(",")
               .append(escapeCsv(feeCategory)).append(",")
               .append(escapeCsv(ageGroup)).append(",")
               .append(dueDate).append(",")
               .append(amount).append(",")
               .append(status).append(",")
               .append(escapeCsv(txnRef)).append(",")
               .append(createdAt).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}


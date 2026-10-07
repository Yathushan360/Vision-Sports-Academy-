package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        return new FinancialSummaryDTO(
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
        Map<String, BigDecimal> revenueByType = new HashMap<>();

        for (Payment payment : paidPayments) {
            String feeTypeName = (payment.getFeeStructure() != null && payment.getFeeStructure().getFeeType() != null)
                    ? payment.getFeeStructure().getFeeType().name()
                    : "GENERAL_TRAINING";

            BigDecimal currentSum = revenueByType.getOrDefault(feeTypeName, BigDecimal.ZERO);
            revenueByType.put(feeTypeName, currentSum.add(payment.getAmount()));
        }

        return revenueByType;
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

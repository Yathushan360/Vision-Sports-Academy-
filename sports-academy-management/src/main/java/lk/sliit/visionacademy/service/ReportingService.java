package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

  
}

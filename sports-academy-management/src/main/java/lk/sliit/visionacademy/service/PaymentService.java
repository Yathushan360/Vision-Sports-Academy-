package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import lk.sliit.visionacademy.repository.FeeStructureRepository;
import lk.sliit.visionacademy.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Control Class for the Billing & Payments module.
 * Encapsulates financial business logic, tracks payment statuses,
 * and coordinates between entities and repository access.
 */
@Service
public class PaymentService implements IPaymentService {

    // STATIC MEMBER: Shared constant default penalty across all instances
    public static final BigDecimal LATE_FEE_PENALTY = new BigDecimal("500.00");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    /**
     * Generates monthly training fee for a player with default 7-day grace period.
     * Demonstrates Method Overloading (compile-time polymorphism).
     */
    @Override
    public Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException {
        return generateMonthlyFee(playerId, feeId, LocalDate.now().plusDays(7));
    }

    /**
     * Overloaded method: Generates monthly training fee with a custom due date.
     */
    @Override
    public Payment generateMonthlyFee(Integer playerId, Integer feeId, LocalDate dueDate) throws PaymentProcessingException {
        FeeStructure fee = feeStructureRepository.findById(feeId)
                .orElseThrow(() -> new PaymentProcessingException("Invalid Fee ID: " + feeId));

        Payment payment = new Payment();
        payment.setPlayerId(playerId);
        payment.setFeeStructure(fee);
        payment.setAmount(fee.getAmount());
        payment.setDueDate(dueDate);
        payment.setStatus(PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    /**
     * Retrieves all payments.
     */
    @Override
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Overloaded method: Retrieves payments filtered by status.
     */
    @Override
    public List<Payment> getPayments(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    /**
     * Overloaded method: Retrieves payments filtered by player ID.
     */
    @Override
    public List<Payment> getPayments(Integer playerId) {
        return getPaymentsByPlayer(playerId);
    }

    @Override
    public List<Payment> getPaymentsByPlayer(Integer playerId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .collect(Collectors.toList());
    }

    /**
     * Triggers overdue calculations using the default static penalty.
     */
    @Override
    public void calculateOverduePayments() {
        calculateOverduePayments(LATE_FEE_PENALTY);
    }

    /**
     * Overloaded method: Triggers overdue calculations with a custom penalty amount.
     */
    @Override
    public void calculateOverduePayments(BigDecimal penalty) {
        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        for (Payment payment : pendingPayments) {
            if (payment.getDueDate().isBefore(LocalDate.now())) {
                payment.setStatus(PaymentStatus.OVERDUE);
                payment.setAmount(payment.getAmount().add(penalty));
                paymentRepository.save(payment);
            }
        }
    }
}

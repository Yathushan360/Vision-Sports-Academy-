package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.exception.InvalidPaymentStateException;
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
 * enforces finite state transitions, and coordinates between entities and repository access.
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
     * Processes a payment transaction using PaymentProcessRequest DTO.
     * Enforces finite state management (preventing re-payment of PAID records)
     * and validates domain business invariants.
     */
    @Override
    public Payment processPayment(PaymentProcessRequest request) throws PaymentProcessingException, InvalidPaymentStateException {
        if (request == null || request.getPaymentId() == null) {
            throw new PaymentProcessingException("Payment request and Payment ID must not be null");
        }

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentProcessingException("Payment record not found with ID: " + request.getPaymentId()));

        // Finite State Management & Domain Invariant:
        // An invoice that has already reached the PAID state cannot be re-settled.
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new InvalidPaymentStateException(
                    "Payment #" + payment.getPaymentId() + " has already been settled and marked as PAID on " + payment.getPaidDate()
            );
        }

        // Domain Invariant: Payment amount validation
        if (request.getAmount() != null && request.getAmount().compareTo(payment.getAmount()) < 0) {
            throw new PaymentProcessingException(
                    "Insufficient payment amount for Payment #" + payment.getPaymentId() +
                    ". Outstanding: Rs. " + payment.getAmount() + ", Provided: Rs. " + request.getAmount()
            );
        }

        // State Transition: PENDING / OVERDUE -> PAID
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidDate(request.getPaidDate() != null ? request.getPaidDate() : LocalDate.now());

        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank()) {
            payment.setPaymentMethod(request.getPaymentMethod());
        } else {
            payment.setPaymentMethod("CASH");
        }

        if (request.getTransactionReference() != null && !request.getTransactionReference().isBlank()) {
            payment.setTransactionReference(request.getTransactionReference());
        }

        return paymentRepository.save(payment);
    }

    /**
     * Overloaded method: Processes a payment directly by paymentId with default settings.
     * Demonstrates Method Overloading (compile-time polymorphism).
     */
    @Override
    public Payment processPayment(Integer paymentId) throws PaymentProcessingException, InvalidPaymentStateException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentProcessingException("Payment record not found with ID: " + paymentId));

        PaymentProcessRequest request = new PaymentProcessRequest();
        request.setPaymentId(paymentId);
        request.setAmount(payment.getAmount());
        request.setPaymentMethod("CASH");
        request.setPaidDate(LocalDate.now());

        return processPayment(request);
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

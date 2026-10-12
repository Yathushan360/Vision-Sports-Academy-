package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentReminder;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.entity.ReminderChannel;
import lk.sliit.visionacademy.exception.FeeTierNotFoundException;
import lk.sliit.visionacademy.exception.InsufficientPaymentAmountException;
import lk.sliit.visionacademy.exception.InvalidPaymentStateException;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import lk.sliit.visionacademy.notification.AbstractNotificationDispatcher;
import lk.sliit.visionacademy.notification.EmailNotificationDispatcher;
import lk.sliit.visionacademy.notification.SmsNotificationDispatcher;
import lk.sliit.visionacademy.repository.FeeStructureRepository;
import lk.sliit.visionacademy.repository.PaymentReminderRepository;
import lk.sliit.visionacademy.repository.PaymentRepository;
import lk.sliit.visionacademy.util.FinancialRulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * Control Class for the Billing & Payments module.
 * Encapsulates financial business logic, tracks payment statuses,
 * enforces finite state transitions, coordinates between entities,
 * and manages notification dispatch & reminder auditing.
 */
@Service
public class PaymentService implements IPaymentService {

    // STATIC MEMBER: Shared constant referenced from class-level rules engine (Lecture 05/10)
    public static final BigDecimal LATE_FEE_PENALTY = FinancialRulesEngine.STANDARD_LATE_FEE_PENALTY;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private PaymentReminderRepository paymentReminderRepository;

    @Autowired(required = false)
    private SmsNotificationDispatcher smsDispatcher;

    @Autowired(required = false)
    private EmailNotificationDispatcher emailDispatcher;

    /**
     * Demonstrates Runtime Polymorphism (Lecture 05/10 Slides 11, 26, 27):
     * Superclass reference AbstractNotificationDispatcher refers to concrete subclass instance.
     */
    private AbstractNotificationDispatcher resolveDispatcher(ReminderChannel channel) {
        if (channel == ReminderChannel.EMAIL && emailDispatcher != null) {
            return emailDispatcher;
        }
        return smsDispatcher != null ? smsDispatcher : new SmsNotificationDispatcher();
    }

    @Override
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    /**
     * Generates monthly training fee for a player with default grace period.
     * Demonstrates Method Overloading and Static Method usage.
     */
    @Override
    public Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException {
        return generateMonthlyFee(playerId, feeId, FinancialRulesEngine.calculateDefaultDueDate(LocalDate.now()));
    }

    /**
     * Overloaded method: Generates monthly training fee with a custom due date.
     */
    @Override
    public Payment generateMonthlyFee(Integer playerId, Integer feeId, LocalDate dueDate) throws PaymentProcessingException {
        FeeStructure fee = feeStructureRepository.findById(feeId)
                .orElseThrow(() -> new FeeTierNotFoundException("Invalid Fee Tier ID: " + feeId, feeId));

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

        // Domain Invariant: Payment amount validation with specialized checked exception
        if (request.getAmount() != null && request.getAmount().compareTo(payment.getAmount()) < 0) {
            throw new InsufficientPaymentAmountException(
                    "Insufficient payment amount for Payment #" + payment.getPaymentId() +
                    ". Outstanding: " + FinancialRulesEngine.formatCurrency(payment.getAmount()) +
                    ", Provided: " + FinancialRulesEngine.formatCurrency(request.getAmount()),
                    payment.getAmount(),
                    request.getAmount()
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

    @Override
    public Payment getPaymentById(Integer paymentId) throws PaymentProcessingException {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentProcessingException("Payment record not found with ID: " + paymentId));
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
            if (FinancialRulesEngine.isOverdue(payment.getDueDate(), LocalDate.now())) {
                payment.setStatus(PaymentStatus.OVERDUE);
                BigDecimal updatedAmount = (penalty != null)
                        ? payment.getAmount().add(penalty)
                        : FinancialRulesEngine.applyLateFee(payment.getAmount());
                payment.setAmount(updatedAmount);
                paymentRepository.save(payment);
            }
        }
    }

    /**
     * Java Collection Framework: Queue & PriorityQueue (Lecture 07 Slides 25-32).
     * Organizes overdue invoices into a heap-ordered PriorityQueue where payments with
     * higher debt balances are positioned at the head of the queue.
     */
    @Override
    public PriorityQueue<Payment> getPrioritizedOverdueQueue() {
        calculateOverduePayments();
        List<Payment> overduePayments = paymentRepository.findByStatus(PaymentStatus.OVERDUE);

        // Custom Comparator: Descending order by debt amount (highest balance first)
        PriorityQueue<Payment> queue = new PriorityQueue<>(
                (p1, p2) -> {
                    BigDecimal a1 = (p1.getAmount() != null) ? p1.getAmount() : BigDecimal.ZERO;
                    BigDecimal a2 = (p2.getAmount() != null) ? p2.getAmount() : BigDecimal.ZERO;
                    return a2.compareTo(a1);
                }
        );

        queue.addAll(overduePayments);
        return queue;
    }

    /**
     * Extracts overdue payments in descending order of financial priority.
     * Demonstrates Queue polling (FIFO according to priority ordering).
     */
    @Override
    public List<Payment> getPrioritizedOverduePayments() {
        PriorityQueue<Payment> queue = new PriorityQueue<>(getPrioritizedOverdueQueue());
        List<Payment> prioritizedList = new ArrayList<>();
        while (!queue.isEmpty()) {
            prioritizedList.add(queue.poll());
        }
        return prioritizedList;
    }

    /**
     * Batch triggers payment reminder notifications for all currently OVERDUE fee records.
     * Utilizes PriorityQueue<Payment> (Lecture 07) to ensure highest-risk overdue debts
     * are alerted first before smaller accounts.
     * Records each notification attempt in the database for auditing and history tracking.
     */
    @Override
    public int sendOverdueReminders() {
        // Ensure overdue checks are updated first
        calculateOverduePayments();

        // PriorityQueue orders receivables by highest balance first
        PriorityQueue<Payment> priorityQueue = getPrioritizedOverdueQueue();
        int dispatchedCount = 0;

        while (!priorityQueue.isEmpty()) {
            Payment payment = priorityQueue.poll();

            // Polymorphic dispatch via template method
            AbstractNotificationDispatcher dispatcher = resolveDispatcher(ReminderChannel.SMS);
            dispatcher.dispatch(payment, "Notice: Your academy training fee is overdue. Please settle immediately.");

            PaymentReminder reminder = new PaymentReminder(payment, ReminderChannel.SMS);
            paymentReminderRepository.save(reminder);
            dispatchedCount++;
        }

        return dispatchedCount;
    }

    /**
     * Dispatches a single reminder notification for a given payment record.
     * Demonstrates Nested try Statements and Finally Block (Lecture 10/11 Slides 33-40).
     */
    @Override
    public PaymentReminder sendReminder(Integer paymentId, ReminderChannel channel) throws PaymentProcessingException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentProcessingException("Payment not found with ID: " + paymentId));

        ReminderChannel selectedChannel = (channel != null) ? channel : ReminderChannel.SMS;

        // OUTER TRY BLOCK (Lecture 10/11 Slides 33-35): Manages database audit persistence
        try {
            // INNER TRY BLOCK (Lecture 10/11 Slides 33-35): Monitors external network/gateway communication
            try {
                AbstractNotificationDispatcher dispatcher = resolveDispatcher(selectedChannel);
                dispatcher.dispatch(payment, "Official Reminder: Academy fee payment of Rs. " + payment.getAmount() + " is pending.");
            } catch (RuntimeException gatewayEx) {
                // Inner catch: Prevents transient delivery glitch from crashing database audit
                System.err.println("[GATEWAY EXCEPTION CAUGHT IN INNER TRY] Transmission failed: " + gatewayEx.getMessage());
            }

            PaymentReminder reminder = new PaymentReminder(payment, selectedChannel);
            return paymentReminderRepository.save(reminder);

        } catch (Exception dbEx) {
            // Outer catch: Intercepts persistence failure and propagates checked domain exception
            throw new PaymentProcessingException("Database error saving reminder audit log for Payment #" + paymentId, dbEx);
        } finally {
            // FINALLY BLOCK (Lecture 10/11 Slides 36-40): Always executes regardless of exceptions
            System.out.println("[AUDIT FINALLY] sendReminder cycle finalized for Payment #" + paymentId);
        }
    }

    /**
     * Retrieves the history of reminder notifications sent for a specific payment.
     */
    @Override
    public List<PaymentReminder> getReminderLogs(Integer paymentId) {
        return paymentReminderRepository.findByPayment_PaymentId(paymentId);
    }
}

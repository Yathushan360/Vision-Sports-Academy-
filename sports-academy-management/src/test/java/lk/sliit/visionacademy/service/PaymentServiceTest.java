package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.entity.*;
import lk.sliit.visionacademy.exception.FeeTierNotFoundException;
import lk.sliit.visionacademy.exception.InsufficientPaymentAmountException;
import lk.sliit.visionacademy.exception.InvalidPaymentStateException;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import lk.sliit.visionacademy.repository.FeeStructureRepository;
import lk.sliit.visionacademy.repository.PaymentReminderRepository;
import lk.sliit.visionacademy.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test Suite for PaymentService (Control Class).
 * Demonstrates Verification & Validation (V&V), Test-Driven Design (TDD),
 * and verifies domain business invariants and exception handling.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FeeStructureRepository feeStructureRepository;

    @Mock
    private PaymentReminderRepository paymentReminderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private FeeStructure sampleFee;
    private Payment samplePendingPayment;

    @BeforeEach
    void setUp() {
        sampleFee = new FeeStructure();
        sampleFee.setFeeId(101);
        sampleFee.setFeeType(FeeType.MONTHLY_TRAINING);
        sampleFee.setAmount(new BigDecimal("2500.00"));
        sampleFee.setAgeGroup("U16");
        sampleFee.setEffectiveFrom(LocalDate.now());

        samplePendingPayment = new Payment();
        samplePendingPayment.setPaymentId(1);
        samplePendingPayment.setPlayerId(1024);
        samplePendingPayment.setFeeStructure(sampleFee);
        samplePendingPayment.setAmount(new BigDecimal("2500.00"));
        samplePendingPayment.setDueDate(LocalDate.now().plusDays(7));
        samplePendingPayment.setStatus(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Verify fee generation correctly populates defaults and PENDING status")
    void testGenerateMonthlyFee_Success() throws PaymentProcessingException {
        when(feeStructureRepository.findById(101)).thenReturn(Optional.of(sampleFee));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment created = paymentService.generateMonthlyFee(1024, 101);

        assertNotNull(created);
        assertEquals(1024, created.getPlayerId());
        assertEquals(new BigDecimal("2500.00"), created.getAmount());
        assertEquals(PaymentStatus.PENDING, created.getStatus());
        assertEquals(LocalDate.now().plusDays(7), created.getDueDate());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Verify fee generation throws FeeTierNotFoundException when fee ID does not exist (Lecture 10/11)")
    void testGenerateMonthlyFee_InvalidFeeId_ThrowsException() {
        when(feeStructureRepository.findById(999)).thenReturn(Optional.empty());

        FeeTierNotFoundException exception = assertThrows(
                FeeTierNotFoundException.class,
                () -> paymentService.generateMonthlyFee(1024, 999)
        );

        assertTrue(exception.getMessage().contains("999"));
        assertEquals(999, exception.getFeeId());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Verify payment settlement transitions state to PAID and records transaction metadata")
    void testProcessPayment_Success() throws PaymentProcessingException, InvalidPaymentStateException {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(samplePendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentProcessRequest request = new PaymentProcessRequest(
                1,
                new BigDecimal("2500.00"),
                "ONLINE",
                LocalDate.now(),
                "TXN-ONLINE-987",
                "Settled via web portal"
        );

        Payment processed = paymentService.processPayment(request);

        assertEquals(PaymentStatus.PAID, processed.getStatus());
        assertEquals("ONLINE", processed.getPaymentMethod());
        assertEquals("TXN-ONLINE-987", processed.getTransactionReference());
        assertNotNull(processed.getPaidDate());
        verify(paymentRepository, times(1)).save(samplePendingPayment);
    }

    @Test
    @DisplayName("Verify double payment attempt triggers InvalidPaymentStateException (Finite State Protection)")
    void testProcessPayment_AlreadyPaid_ThrowsInvalidPaymentStateException() {
        samplePendingPayment.setStatus(PaymentStatus.PAID);
        samplePendingPayment.setPaidDate(LocalDate.now().minusDays(1));
        when(paymentRepository.findById(1)).thenReturn(Optional.of(samplePendingPayment));

        PaymentProcessRequest request = new PaymentProcessRequest(
                1,
                new BigDecimal("2500.00"),
                "CASH",
                LocalDate.now(),
                "TXN-123",
                null
        );

        InvalidPaymentStateException exception = assertThrows(
                InvalidPaymentStateException.class,
                () -> paymentService.processPayment(request)
        );

        assertTrue(exception.getMessage().contains("already been settled"));
        verify(paymentRepository, never()).save(samplePendingPayment);
    }

    @Test
    @DisplayName("Verify underpayment triggers InsufficientPaymentAmountException with amounts (Lecture 10/11)")
    void testProcessPayment_InsufficientAmount_ThrowsException() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(samplePendingPayment));

        PaymentProcessRequest request = new PaymentProcessRequest(
                1,
                new BigDecimal("1000.00"), // Outstanding is 2500.00
                "CASH",
                LocalDate.now(),
                "TXN-123",
                null
        );

        InsufficientPaymentAmountException exception = assertThrows(
                InsufficientPaymentAmountException.class,
                () -> paymentService.processPayment(request)
        );

        assertTrue(exception.getMessage().contains("Insufficient payment amount"));
        assertEquals(new BigDecimal("2500.00"), exception.getRequiredAmount());
        assertEquals(new BigDecimal("1000.00"), exception.getProvidedAmount());
        verify(paymentRepository, never()).save(samplePendingPayment);
    }

    @Test
    @DisplayName("Verify overdue assessment applies static penalty of Rs. 500 and transitions to OVERDUE")
    void testCalculateOverduePayments_AppliesLateFeePenalty() {
        Payment overduePayment = new Payment();
        overduePayment.setPaymentId(2);
        overduePayment.setAmount(new BigDecimal("2500.00"));
        overduePayment.setDueDate(LocalDate.now().minusDays(2)); // Past due date
        overduePayment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByStatus(PaymentStatus.PENDING))
                .thenReturn(Collections.singletonList(overduePayment));

        paymentService.calculateOverduePayments();

        assertEquals(PaymentStatus.OVERDUE, overduePayment.getStatus());
        // 2500.00 + 500.00 = 3000.00
        assertEquals(new BigDecimal("3000.00"), overduePayment.getAmount());
        verify(paymentRepository, times(1)).save(overduePayment);
    }

    @Test
    @DisplayName("Verify overdue reminder dispatch creates audit record in payment_reminders")
    void testSendOverdueReminders_DispatchesAndAuditsNotification() {
        Payment overduePayment = new Payment();
        overduePayment.setPaymentId(5);
        overduePayment.setPlayerId(2048);
        overduePayment.setStatus(PaymentStatus.OVERDUE);

        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(Collections.emptyList());
        when(paymentRepository.findByStatus(PaymentStatus.OVERDUE)).thenReturn(Collections.singletonList(overduePayment));

        int remindersSent = paymentService.sendOverdueReminders();

        assertEquals(1, remindersSent);
        verify(paymentReminderRepository, times(1)).save(any(PaymentReminder.class));
    }

    @Test
    @DisplayName("Verify AbstractNotificationDispatcher template method and polymorphism (Lecture 05/10)")
    void testAbstractNotificationDispatcher_TemplateMethodExecution() {
        lk.sliit.visionacademy.notification.AbstractNotificationDispatcher sms =
                new lk.sliit.visionacademy.notification.SmsNotificationDispatcher();
        lk.sliit.visionacademy.notification.AbstractNotificationDispatcher email =
                new lk.sliit.visionacademy.notification.EmailNotificationDispatcher();

        assertEquals("SMS", sms.getChannelName());
        assertEquals(3, sms.getRetryLimit());
        assertEquals("EMAIL", email.getChannelName());
        assertEquals(5, email.getRetryLimit());

        // Template method dispatch calls concrete pre/post hooks and overridden sendPayload
        assertTrue(sms.dispatch(samplePendingPayment, "Test SMS reminder"));
        assertTrue(email.dispatch(samplePendingPayment, "Test Email reminder"));
        assertFalse(sms.dispatch(null, "Null payment should fail gracefully"));
    }

    @Test
    @DisplayName("Verify FinancialRulesEngine static initialization block and static methods (Lecture 05/10 Slides 20-33)")
    void testFinancialRulesEngine_StaticInitializationAndMethods() {
        // Direct access to static variables without creating an object (Slide 20, 27)
        assertEquals("LKR", lk.sliit.visionacademy.util.FinancialRulesEngine.ACADEMY_CURRENCY);
        assertEquals(7, lk.sliit.visionacademy.util.FinancialRulesEngine.DEFAULT_GRACE_PERIOD_DAYS);
        assertEquals(new BigDecimal("500.00"), lk.sliit.visionacademy.util.FinancialRulesEngine.STANDARD_LATE_FEE_PENALTY);
        assertEquals(new BigDecimal("250.00"), lk.sliit.visionacademy.util.FinancialRulesEngine.REGISTRATION_SURCHARGE);

        // Calling static methods directly using the class name (Slide 29)
        LocalDate today = LocalDate.now();
        assertEquals(today.plusDays(7), lk.sliit.visionacademy.util.FinancialRulesEngine.calculateDefaultDueDate(today));

        BigDecimal fee = new BigDecimal("2500.00");
        BigDecimal withLateFee = lk.sliit.visionacademy.util.FinancialRulesEngine.applyLateFee(fee);
        assertEquals(new BigDecimal("3000.00"), withLateFee);

        assertTrue(lk.sliit.visionacademy.util.FinancialRulesEngine.isOverdue(today.minusDays(1), today));
        assertFalse(lk.sliit.visionacademy.util.FinancialRulesEngine.isOverdue(today.plusDays(1), today));

        assertEquals("Rs. 2,500.00", lk.sliit.visionacademy.util.FinancialRulesEngine.formatCurrency(fee));
        assertNotNull(lk.sliit.visionacademy.util.FinancialRulesEngine.getRegulatoryRates());
    }

    @Test
    @DisplayName("Verify sendReminder nested try statement and audit persistence (Lecture 10/11 Slides 33-40)")
    void testSendReminder_Success_SavesAuditRecord() throws PaymentProcessingException {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(samplePendingPayment));
        when(paymentReminderRepository.save(any(PaymentReminder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentReminder reminder = paymentService.sendReminder(1, ReminderChannel.SMS);

        assertNotNull(reminder);
        assertEquals(samplePendingPayment, reminder.getPayment());
        assertEquals(ReminderChannel.SMS, reminder.getChannel());
        verify(paymentReminderRepository, times(1)).save(any(PaymentReminder.class));
    }

    @Test
    @DisplayName("Verify sendReminder outer catch intercepts DB error and wraps in PaymentProcessingException (Lecture 10/11)")
    void testSendReminder_DatabaseFailure_PropagatesPaymentProcessingException() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(samplePendingPayment));
        when(paymentReminderRepository.save(any(PaymentReminder.class))).thenThrow(new RuntimeException("DB Connection Timeout"));

        PaymentProcessingException exception = assertThrows(
                PaymentProcessingException.class,
                () -> paymentService.sendReminder(1, ReminderChannel.SMS)
        );

        assertTrue(exception.getMessage().contains("Database error saving reminder audit log"));
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("Verify PriorityQueue orders overdue payments by highest debt first (Lecture 07 Slides 25-32)")
    void testPriorityQueue_OrdersByHighestDebtFirst() {
        Payment p1 = new Payment();
        p1.setPaymentId(10);
        p1.setAmount(new BigDecimal("1500.00"));
        p1.setStatus(PaymentStatus.OVERDUE);

        Payment p2 = new Payment();
        p2.setPaymentId(20);
        p2.setAmount(new BigDecimal("5000.00")); // Largest debt
        p2.setStatus(PaymentStatus.OVERDUE);

        Payment p3 = new Payment();
        p3.setPaymentId(30);
        p3.setAmount(new BigDecimal("2500.00"));
        p3.setStatus(PaymentStatus.OVERDUE);

        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(Collections.emptyList());
        when(paymentRepository.findByStatus(PaymentStatus.OVERDUE)).thenReturn(java.util.Arrays.asList(p1, p2, p3));

        java.util.PriorityQueue<Payment> queue = paymentService.getPrioritizedOverdueQueue();

        assertNotNull(queue);
        assertEquals(3, queue.size());
        // Polling removes in priority order (highest debt first)
        assertEquals(20, queue.poll().getPaymentId()); // 5000.00
        assertEquals(30, queue.poll().getPaymentId()); // 2500.00
        assertEquals(10, queue.poll().getPaymentId()); // 1500.00
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("Verify Composition Relationship: Payment owns PaymentAuditTrail lifecycle (Lecture 04/06.1)")
    void testComposition_PaymentOwnsPaymentAuditTrailLifecycle() {
        Payment payment = new Payment();
        payment.setPaymentId(55);
        payment.setPlayerId(999);
        payment.setAmount(new BigDecimal("3500.00"));

        // Composition: Add audit trail parts to the payment whole
        payment.addAuditTrail("INVOICE_GENERATED", "Monthly invoice generated for player #999");
        payment.addAuditTrail("REMINDER_DISPATCHED", "SMS reminder sent");

        assertNotNull(payment.getAuditTrail());
        assertEquals(2, payment.getAuditTrail().size());

        // Verify Part points back to the Whole (strong ownership)
        PaymentAuditTrail trail1 = payment.getAuditTrail().get(0);
        assertEquals("INVOICE_GENERATED", trail1.getAction());
        assertEquals(payment, trail1.getPayment());
        assertNotNull(trail1.getRecordedAt());

        PaymentAuditTrail trail2 = payment.getAuditTrail().get(1);
        assertEquals("REMINDER_DISPATCHED", trail2.getAction());
        assertEquals(payment, trail2.getPayment());
    }

    @Test
    @DisplayName("Verify Dependency Relationship: PaymentReceiptPrinter uses Payment temporarily (Lecture 04/06.1)")
    void testDependency_PaymentReceiptPrinter_UsesPaymentWithoutState() {
        PaymentReceiptPrinter printer = new PaymentReceiptPrinter();

        // Dependency: Payment object passed as a method parameter (Uses-A relationship)
        String receipt = printer.printReceipt(samplePendingPayment);

        assertNotNull(receipt);
        assertTrue(receipt.contains("VISION SPORTS ACADEMY"));
        assertTrue(receipt.contains("Player #1024"));
        assertTrue(receipt.contains("Rs. 2,500.00"));
        assertTrue(receipt.contains("MONTHLY_TRAINING"));

        // Null defensive verification
        String nullReceipt = printer.printReceipt(null);
        assertTrue(nullReceipt.contains("ERROR"));
    }

    @Test
    @DisplayName("Verify Aggregation Relationship: FeeStructure exists independently of Payment (Lecture 04/06.1)")
    void testAggregation_PaymentAndFeeStructure_IndependentLifecycle() {
        // FeeStructure exists independently in the catalog
        FeeStructure independentFee = new FeeStructure();
        independentFee.setFeeId(500);
        independentFee.setAmount(new BigDecimal("4000.00"));
        independentFee.setAgeGroup("U18");

        Payment paymentA = new Payment();
        paymentA.setPaymentId(101);
        paymentA.setFeeStructure(independentFee); // Aggregation reference

        Payment paymentB = new Payment();
        paymentB.setPaymentId(102);
        paymentB.setFeeStructure(independentFee); // Shared aggregation reference

        // Both payments reference the same catalog tier
        assertSame(independentFee, paymentA.getFeeStructure());
        assertSame(independentFee, paymentB.getFeeStructure());

        // Deleting or completing paymentA does not destroy independentFee or paymentB
        paymentA.setFeeStructure(null);
        assertNotNull(independentFee);
        assertSame(independentFee, paymentB.getFeeStructure());
        assertEquals(500, independentFee.getFeeId());
    }
}


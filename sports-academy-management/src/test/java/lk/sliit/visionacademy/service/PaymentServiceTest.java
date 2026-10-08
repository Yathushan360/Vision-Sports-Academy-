package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.entity.*;
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
    @DisplayName("Verify fee generation throws PaymentProcessingException when fee ID does not exist")
    void testGenerateMonthlyFee_InvalidFeeId_ThrowsException() {
        when(feeStructureRepository.findById(999)).thenReturn(Optional.empty());

        PaymentProcessingException exception = assertThrows(
                PaymentProcessingException.class,
                () -> paymentService.generateMonthlyFee(1024, 999)
        );

        assertTrue(exception.getMessage().contains("Invalid Fee ID: 999"));
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

   
}

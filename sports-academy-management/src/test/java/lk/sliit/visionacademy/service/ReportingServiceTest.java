package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.FinancialSummaryDTO;
import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.FeeType;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test Suite for ReportingService & Collection Framework Integration (Lecture 07).
 * Verifies TreeSet sorted order, HashSet deduplication & O(1) lookups,
 * and LinkedHashMap insertion-order preservation.
 */
@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private IPaymentService paymentService;

    @InjectMocks
    private ReportingService reportingService;

    private Payment p1;
    private Payment p2;
    private Payment p3;
    private Payment p4;

    @BeforeEach
    void setUp() {
        FeeStructure feeTraining = new FeeStructure();
        feeTraining.setFeeType(FeeType.MONTHLY_TRAINING);

        FeeStructure feeReg = new FeeStructure();
        feeReg.setFeeType(FeeType.REGISTRATION);

        // Payment 1: Player 1024, Overdue, Due 2026-10-01
        p1 = new Payment();
        p1.setPaymentId(1);
        p1.setPlayerId(1024);
        p1.setAmount(new BigDecimal("2500.00"));
        p1.setDueDate(LocalDate.of(2026, 10, 1));
        p1.setStatus(PaymentStatus.OVERDUE);
        p1.setFeeStructure(feeTraining);

        // Payment 2: Player 1024 (same player), Overdue, Due 2026-09-15 (older date)
        p2 = new Payment();
        p2.setPaymentId(2);
        p2.setPlayerId(1024);
        p2.setAmount(new BigDecimal("1500.00"));
        p2.setDueDate(LocalDate.of(2026, 9, 15));
        p2.setStatus(PaymentStatus.OVERDUE);
        p2.setFeeStructure(feeTraining);

        // Payment 3: Player 2048, Pending, Due 2026-10-20
        p3 = new Payment();
        p3.setPaymentId(3);
        p3.setPlayerId(2048);
        p3.setAmount(new BigDecimal("3000.00"));
        p3.setDueDate(LocalDate.of(2026, 10, 20));
        p3.setStatus(PaymentStatus.PENDING);
        p3.setFeeStructure(feeReg);

        // Payment 4: Player 3000, Paid, Due 2026-10-01 (duplicate due date with p1)
        p4 = new Payment();
        p4.setPaymentId(4);
        p4.setPlayerId(3000);
        p4.setAmount(new BigDecimal("5000.00"));
        p4.setDueDate(LocalDate.of(2026, 10, 1));
        p4.setStatus(PaymentStatus.PAID);
        p4.setFeeStructure(feeReg);
    }

    @Test
    @DisplayName("Verify TreeSet<LocalDate> automatically sorts chronologically and deduplicates (Lecture 07 Slides 20-24)")
    void testTreeSet_DistinctDueDatesSortedChronologically() {
        // Return unsorted list with duplicate dates: 2026-10-01, 2026-09-15, 2026-10-20, 2026-10-01
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3, p4));

        TreeSet<LocalDate> sortedDates = reportingService.getDistinctDueDatesSorted();

        assertNotNull(sortedDates);
        // Duplicate 2026-10-01 is removed: size should be 3
        assertEquals(3, sortedDates.size());

        // Chronological order guaranteed: earliest first
        assertEquals(LocalDate.of(2026, 9, 15), sortedDates.first());
        assertEquals(LocalDate.of(2026, 10, 20), sortedDates.last());

        // Verify iteration order is strictly ascending
        Iterator<LocalDate> it = sortedDates.iterator();
        assertEquals(LocalDate.of(2026, 9, 15), it.next());
        assertEquals(LocalDate.of(2026, 10, 1), it.next());
        assertEquals(LocalDate.of(2026, 10, 20), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    @DisplayName("Verify HashSet<Integer> deduplicates debtors and provides O(1) membership check (Lecture 07 Slides 19-21)")
    void testHashSet_UniqueDebtorPlayerIds_DeduplicationAndFastLookup() {
        // Outstanding report returns p1 (Player 1024), p2 (Player 1024), p3 (Player 2048)
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3, p4));

        Set<Integer> uniqueDebtors = reportingService.getUniqueDebtorPlayerIds();

        assertNotNull(uniqueDebtors);
        // Player 1024 had 2 invoices, but HashSet ensures only 1 entry
        assertEquals(2, uniqueDebtors.size());
        assertTrue(uniqueDebtors.contains(1024));
        assertTrue(uniqueDebtors.contains(2048));
        assertFalse(uniqueDebtors.contains(3000)); // Player 3000 is PAID

        // Verify isPlayerInArrears O(1) lookup
        assertTrue(reportingService.isPlayerInArrears(1024));
        assertTrue(reportingService.isPlayerInArrears(2048));
        assertFalse(reportingService.isPlayerInArrears(3000));
        assertFalse(reportingService.isPlayerInArrears(9999));
        assertFalse(reportingService.isPlayerInArrears(null));
    }

    @Test
    @DisplayName("Verify LinkedHashMap maintains exact insertion order for aging schedule (Lecture 07 Slides 33-38)")
    void testLinkedHashMap_AgingSchedule_PreservesInsertionOrder() {
        when(paymentRepository.findByStatus(PaymentStatus.OVERDUE)).thenReturn(Arrays.asList(p1, p2));

        Map<String, BigDecimal> agingSchedule = reportingService.getAgingSchedule();

        assertNotNull(agingSchedule);
        assertTrue(agingSchedule instanceof LinkedHashMap, "Aging schedule must be backed by LinkedHashMap to preserve insertion sequence");
        assertEquals(3, agingSchedule.size());

        // Verify sequential iteration order
        List<String> keys = new ArrayList<>(agingSchedule.keySet());
        assertEquals("1 - 7 Days (Grace Period)", keys.get(0));
        assertEquals("8 - 30 Days (Moderate Arrears)", keys.get(1));
        assertEquals("31+ Days (Critical Arrears)", keys.get(2));
    }

    @Test
    @DisplayName("Verify LinkedHashMap preserves insertion order for revenue category breakdown (Lecture 07)")
    void testLinkedHashMap_RevenueByFeeType() {
        when(paymentRepository.findByStatus(PaymentStatus.PAID)).thenReturn(Collections.singletonList(p4));

        Map<String, BigDecimal> revenueMap = reportingService.getRevenueByFeeType();

        assertNotNull(revenueMap);
        assertTrue(revenueMap instanceof LinkedHashMap);
        assertTrue(revenueMap.containsKey("REGISTRATION"));
        assertEquals(new BigDecimal("5000.00"), revenueMap.get("REGISTRATION"));
    }

    @Test
    @DisplayName("Verify FinancialSummaryDTO incorporates HashSet & TreeSet metrics")
    void testFinancialSummaryDTO_PopulatedWithCollectionMetrics() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3, p4));

        FinancialSummaryDTO summary = reportingService.getFinancialSummary();

        assertNotNull(summary);
        assertEquals(new BigDecimal("5000.00"), summary.getTotalRevenue());
        assertEquals(new BigDecimal("3000.00"), summary.getTotalPendingAmount());
        assertEquals(new BigDecimal("4000.00"), summary.getTotalOverdueAmount());
        assertEquals(2, summary.getUniqueDebtorsCount()); // 1024, 2048
        assertEquals(3, summary.getDistinctDueDatesCount()); // 3 unique dates
    }
}

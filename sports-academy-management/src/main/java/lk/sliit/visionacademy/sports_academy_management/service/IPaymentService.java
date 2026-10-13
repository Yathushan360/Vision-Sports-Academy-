package lk.sliit.visionacademy.sports_academy_management.service;

import lk.sliit.visionacademy.sports_academy_management.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.sports_academy_management.entity.FeeStructure;
import lk.sliit.visionacademy.sports_academy_management.entity.Payment;
import lk.sliit.visionacademy.sports_academy_management.entity.PaymentReminder;
import lk.sliit.visionacademy.sports_academy_management.entity.PaymentStatus;
import lk.sliit.visionacademy.sports_academy_management.entity.ReminderChannel;
import lk.sliit.visionacademy.sports_academy_management.exception.InvalidPaymentStateException;
import lk.sliit.visionacademy.sports_academy_management.exception.PaymentProcessingException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.PriorityQueue;

public interface IPaymentService {

    // Retrieve active fee tiers/structures for assignment
    List<FeeStructure> getAllFeeStructures();

    // Overloaded: Generate monthly fee with default due date (+7 days)
    Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException;

    // Overloaded: Generate monthly fee with custom due date
    Payment generateMonthlyFee(Integer playerId, Integer feeId, LocalDate dueDate) throws PaymentProcessingException;

    // Process fee payment using structured request DTO
    Payment processPayment(PaymentProcessRequest request) throws PaymentProcessingException, InvalidPaymentStateException;

    // Overloaded: Process fee payment by payment ID (defaults to CASH and current date)
    Payment processPayment(Integer paymentId) throws PaymentProcessingException, InvalidPaymentStateException;

    // Overloaded: Track / retrieve payments
    List<Payment> getPayments();
    List<Payment> getPayments(PaymentStatus status);
    List<Payment> getPayments(Integer playerId);
    List<Payment> getPaymentsByPlayer(Integer playerId);
    Payment getPaymentById(Integer paymentId) throws PaymentProcessingException;

    // Overloaded: Calculate overdue payments with default penalty
    void calculateOverduePayments();

    // Overloaded: Calculate overdue payments with custom penalty
    void calculateOverduePayments(BigDecimal penalty);

    // Reminders & Notification Auditing
    int sendOverdueReminders();
    PaymentReminder sendReminder(Integer paymentId, ReminderChannel channel) throws PaymentProcessingException;
    List<PaymentReminder> getReminderLogs(Integer paymentId);

    // Java Collection Framework (Lecture 07 Slides 25-32):
    // PriorityQueue orders overdue payments by debt magnitude for high-priority recovery
    PriorityQueue<Payment> getPrioritizedOverdueQueue();
    List<Payment> getPrioritizedOverduePayments();
}


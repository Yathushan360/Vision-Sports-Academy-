package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.exception.PaymentProcessingException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {

    // Overloaded: Generate monthly fee with default due date (+7 days)
    Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException;

    // Overloaded: Generate monthly fee with custom due date
    Payment generateMonthlyFee(Integer playerId, Integer feeId, LocalDate dueDate) throws PaymentProcessingException;

    // Overloaded: Track / retrieve payments
    List<Payment> getPayments();
    List<Payment> getPayments(PaymentStatus status);
    List<Payment> getPayments(Integer playerId);
    List<Payment> getPaymentsByPlayer(Integer playerId);

    // Overloaded: Calculate overdue payments with default penalty
    void calculateOverduePayments();

    // Overloaded: Calculate overdue payments with custom penalty
    void calculateOverduePayments(BigDecimal penalty);
}

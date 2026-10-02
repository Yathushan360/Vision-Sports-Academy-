package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.dto.PaymentProcessRequest;
import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.exception.InvalidPaymentStateException;
import lk.sliit.visionacademy.exception.PaymentProcessingException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
   
}

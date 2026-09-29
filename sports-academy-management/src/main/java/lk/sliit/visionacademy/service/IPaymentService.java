package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import java.util.List;

public interface IPaymentService {
    Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException;
    List<Payment> getPayments();
    List<Payment> getPayments(PaymentStatus status);
    List<Payment> getPaymentsByPlayer(Integer playerId);
    void calculateOverduePayments();
}

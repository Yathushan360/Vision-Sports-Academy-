package lk.sliit.visionacademy.service;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import lk.sliit.visionacademy.entity.FeeStructure;
import lk.sliit.visionacademy.exception.PaymentProcessingException;
import lk.sliit.visionacademy.repository.PaymentRepository;
import lk.sliit.visionacademy.repository.FeeStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService implements IPaymentService {

    // STATIC MEMBER: Shared constant across all objects
    public static final BigDecimal LATE_FEE_PENALTY = new BigDecimal("500.00");

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Override
    public Payment generateMonthlyFee(Integer playerId, Integer feeId) throws PaymentProcessingException {
        // Exception Handling using throw for our custom exception
        FeeStructure fee = feeStructureRepository.findById(feeId)
                .orElseThrow(() -> new PaymentProcessingException("Invalid Fee ID: " + feeId));
        
        Payment payment = new Payment();
        payment.setPlayerId(playerId);
        payment.setFeeStructure(fee);
        payment.setAmount(fee.getAmount());
        payment.setDueDate(LocalDate.now().plusDays(7));
        payment.setStatus(PaymentStatus.PENDING);
        
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
    
    @Override
    public List<Payment> getPayments(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Override
    public List<Payment> getPaymentsByPlayer(Integer playerId) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .collect(Collectors.toList());
    }

    @Override
    public void calculateOverduePayments() {
        List<Payment> pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        for (Payment payment : pendingPayments) {
            if (payment.getDueDate().isBefore(LocalDate.now())) {
                payment.setStatus(PaymentStatus.OVERDUE);
                // Applying the static penalty
                payment.setAmount(payment.getAmount().add(LATE_FEE_PENALTY));
                paymentRepository.save(payment);
            }
        }
    }
}

package lk.sliit.visionacademy.repository;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Integer> {

    /**
     * Retrieves all reminder notification records associated with a specific payment.
     */
    List<PaymentReminder> findByPayment(Payment payment);

    /**
     * Retrieves all reminder logs by payment ID.
     */
    List<PaymentReminder> findByPayment_PaymentId(Integer paymentId);
}

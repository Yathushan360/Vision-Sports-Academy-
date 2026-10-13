package lk.sliit.visionacademy.repository;

import lk.sliit.visionacademy.entity.Payment;
import lk.sliit.visionacademy.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Finds payments based on their status (e.g., PENDING, PAID, OVERDUE).
     * 
     * @param status The status to filter payments by.
     * @return A list of payments matching the given status.
     */
    List<Payment> findByStatus(PaymentStatus status);
}

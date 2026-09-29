package lk.sliit.visionacademy.repository;

import lk.sliit.visionacademy.entity.PaymentReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentReminderRepository extends JpaRepository<PaymentReminder, Integer> {
}

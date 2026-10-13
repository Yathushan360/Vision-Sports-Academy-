package lk.sliit.visionacademy.sports_academy_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_reminders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id")
    private Integer reminderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Payment payment;

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private ReminderChannel channel = ReminderChannel.SMS;

    public PaymentReminder() {
    }

    public PaymentReminder(Payment payment, ReminderChannel channel) {
        this.payment = payment;
        this.channel = channel;
        this.sentAt = LocalDateTime.now();
    }

    public Integer getReminderId() {
        return reminderId;
    }

    public void setReminderId(Integer reminderId) {
        this.reminderId = reminderId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public ReminderChannel getChannel() {
        return channel;
    }

    public void setChannel(ReminderChannel channel) {
        this.channel = channel;
    }
}


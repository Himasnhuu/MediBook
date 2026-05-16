package com.medibook.notification.service.impl;

import com.medibook.notification.entity.Notification;
import com.medibook.notification.entity.NotificationType;
import com.medibook.notification.repository.NotificationRepository;
import com.medibook.notification.service.EmailService;
import com.medibook.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    public Notification createAndSend(Notification notification) {
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        try {
            emailService.sendEmail(
                notification.getRecipientEmail(),
                notification.getTitle(),
                notification.getMessage()
            );
            notification.setEmailSent(true);
        } catch (Exception e) {
            notification.setEmailSent(false);
        }
        return notificationRepository.save(notification);
    }

    @Override
    public Notification sendAppointmentBooked(Long userId,
            String recipientEmail, Long appointmentId,
            String doctorName, String date, String time) {

        String title = "Appointment Confirmed!";
        String message = String.format(
            "Your appointment with %s has been successfully booked.<br><br>" +
            "<strong>Appointment ID:</strong> #%d<br>" +
            "<strong>Date:</strong> %s<br>" +
            "<strong>Time:</strong> %s<br><br>" +
            "Please arrive 10 minutes early. " +
            "You can cancel up to 24 hours before your appointment.",
            doctorName, appointmentId, date, time);

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.APPOINTMENT_BOOKED);
    }

    @Override
    public Notification sendAppointmentCancelled(Long userId,
            String recipientEmail, Long appointmentId,
            String doctorName, String date) {

        String title = "Appointment Cancelled";
        String message = String.format(
            "Your appointment with %s on %s (ID: #%d) " +
            "has been cancelled.<br><br>" +
            "If you did not request this cancellation, " +
            "please contact support immediately.<br><br>" +
            "You can book a new appointment anytime through MediBook.",
            doctorName, date, appointmentId);

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.APPOINTMENT_CANCELLED);
    }

    @Override
    public Notification sendAppointmentCompleted(Long userId,
            String recipientEmail, Long appointmentId,
            String doctorName) {

        String title = "Appointment Completed";
        String message = String.format(
            "Your appointment with %s (ID: #%d) " +
            "has been marked as completed.<br><br>" +
            "We hope you had a great experience! " +
            "Please take a moment to rate your doctor on MediBook — " +
            "your feedback helps other patients make informed decisions.<br><br>" +
            "Thank you for choosing MediBook.",
            doctorName, appointmentId);

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.APPOINTMENT_COMPLETED);
    }

    @Override
    public Notification sendPaymentSuccess(Long userId,
            String recipientEmail, String amount,
            String transactionId) {

        String title = "Payment Successful";
        String message = String.format(
            "Your payment has been processed successfully.<br><br>" +
            "<strong>Amount Paid:</strong> ₹%s<br>" +
            "<strong>Transaction ID:</strong> %s<br>" +
            "<strong>Date:</strong> %s<br><br>" +
            "Please keep this transaction ID for your records. " +
            "A detailed receipt is available in your MediBook account.",
            amount, transactionId,
            LocalDateTime.now().toLocalDate().toString());

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.PAYMENT_SUCCESS);
    }

    @Override
    public Notification sendPaymentRefunded(Long userId,
            String recipientEmail, String amount,
            String transactionId) {

        String title = "Refund Processed";
        String message = String.format(
            "Your refund has been successfully processed.<br><br>" +
            "<strong>Refund Amount:</strong> ₹%s<br>" +
            "<strong>Transaction ID:</strong> %s<br><br>" +
            "The amount will be credited to your original payment method " +
            "within 5-7 business days depending on your bank.<br><br>" +
            "If you have any questions, please contact MediBook support.",
            amount, transactionId);

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.PAYMENT_REFUNDED);
    }

    @Override
    public Notification sendReviewReceived(Long userId,
            String recipientEmail, String patientName,
            Double rating) {

        String stars = "⭐".repeat(rating.intValue());
        String title = "New Review Received";
        String message = String.format(
            "You have received a new review from %s.<br><br>" +
            "<strong>Rating:</strong> %s (%.1f / 5.0)<br><br>" +
            "Patient reviews help build your reputation on MediBook. " +
            "Keep up the great work!<br><br>" +
            "Log in to MediBook to view the full review.",
            patientName, stars, rating);

        return buildAndSend(userId, recipientEmail,
                title, message, NotificationType.REVIEW_RECEIVED);
    }

    @Override
    public List<Notification> getByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadByUserId(Long userId) {
        return notificationRepository
                .findByUserIdAndIsRead(userId, false);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndIsRead(userId, false);
    }

    @Override
    public Optional<Notification> getById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Notification not found"));
        notificationRepository.delete(notification);
    }

    private Notification buildAndSend(Long userId,
            String recipientEmail, String title,
            String message, NotificationType type) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setRecipientEmail(recipientEmail);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        return createAndSend(notification);
    }
}
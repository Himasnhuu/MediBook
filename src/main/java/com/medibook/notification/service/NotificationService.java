package com.medibook.notification.service;

import com.medibook.notification.entity.Notification;
import com.medibook.notification.entity.NotificationType;
import java.util.List;
import java.util.Optional;

public interface NotificationService {

	Notification createAndSend(Notification notification);

	Notification sendAppointmentBooked(Long userId, String recipientEmail, Long appointmentId, String doctorName,
			String date, String time);

	Notification sendAppointmentCancelled(Long userId, String recipientEmail, Long appointmentId, String doctorName,
			String date);

	Notification sendAppointmentCompleted(Long userId, String recipientEmail, Long appointmentId, String doctorName);

	Notification sendPaymentSuccess(Long userId, String recipientEmail, String amount, String transactionId);

	Notification sendPaymentRefunded(Long userId, String recipientEmail, String amount, String transactionId);

	Notification sendReviewReceived(Long userId, String recipientEmail, String patientName, Double rating);

	List<Notification> getByUserId(Long userId);

	List<Notification> getUnreadByUserId(Long userId);

	long getUnreadCount(Long userId);

	Optional<Notification> getById(Long id);

	Notification markAsRead(Long id);

	void markAllAsRead(Long userId);

	void deleteNotification(Long id);
}
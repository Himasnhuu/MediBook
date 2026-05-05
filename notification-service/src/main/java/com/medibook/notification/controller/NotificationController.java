package com.medibook.notification.controller;

import com.medibook.notification.entity.Notification;
import com.medibook.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Only ADMIN can create generic notification
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNotification(
            @RequestBody Notification notification) {
        try {
            return ResponseEntity.ok(
                notificationService.createAndSend(notification));
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PATIENT or ADMIN — appointment booked email
    @PostMapping("/appointment-booked")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> appointmentBooked(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendAppointmentBooked(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                Long.parseLong(request.get("appointmentId")),
                request.get("doctorName"),
                request.get("date"),
                request.get("time")
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PATIENT or ADMIN — appointment cancelled email
    @PostMapping("/appointment-cancelled")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> appointmentCancelled(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendAppointmentCancelled(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                Long.parseLong(request.get("appointmentId")),
                request.get("doctorName"),
                request.get("date")
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PROVIDER or ADMIN — appointment completed email
    @PostMapping("/appointment-completed")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> appointmentCompleted(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendAppointmentCompleted(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                Long.parseLong(request.get("appointmentId")),
                request.get("doctorName")
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PATIENT or ADMIN — payment success email
    @PostMapping("/payment-success")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> paymentSuccess(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendPaymentSuccess(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                request.get("amount"),
                request.get("transactionId")
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PATIENT or ADMIN — payment refunded email
    @PostMapping("/payment-refunded")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> paymentRefunded(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendPaymentRefunded(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                request.get("amount"),
                request.get("transactionId")
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // PROVIDER or ADMIN — review received email
    @PostMapping("/review-received")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> reviewReceived(
            @RequestBody Map<String, String> request) {
        try {
            Notification n = notificationService.sendReviewReceived(
                Long.parseLong(request.get("userId")),
                request.get("recipientEmail"),
                request.get("patientName"),
                Double.parseDouble(request.get("rating"))
            );
            return ResponseEntity.ok(n);
        } catch (Exception e) {
            return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    // Public — get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            notificationService.getByUserId(userId));
    }

    // Public — get unread notifications
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            notificationService.getUnreadByUserId(userId));
    }

    // Public — get unread count
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            notificationService.getUnreadCount(userId));
    }

    // Public — get single notification
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(
            @PathVariable Long id) {
        return notificationService.getById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Any authenticated user can mark as read
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id) {
        return ResponseEntity.ok(
            notificationService.markAsRead(id));
    }

    // Any authenticated user can mark all as read
    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("hasRole('PATIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(
            "All notifications marked as read");
    }

    // Only ADMIN can delete a notification
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted");
    }
}
package com.medibook.notification.service.impl;

import com.medibook.notification.entity.Notification;
import com.medibook.notification.entity.NotificationType;
import com.medibook.notification.repository.NotificationRepository;
import com.medibook.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setUserId(1L);
        notification.setRecipientEmail("test@example.com");
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");
        notification.setType(NotificationType.APPOINTMENT_BOOKED);
        notification.setIsRead(false);
        notification.setEmailSent(true);
    }

    // ── createAndSend ──

    @Test
    void createAndSend_Success_EmailSent() throws Exception {
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createAndSend(notification);

        assertNotNull(result);
        assertTrue(result.getEmailSent());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createAndSend_EmailFails_StillSavesNotification() throws Exception {
        doThrow(new RuntimeException("SMTP error"))
            .when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createAndSend(notification);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ── sendAppointmentBooked ──

    @Test
    void sendAppointmentBooked_Success() throws Exception {
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.sendAppointmentBooked(
            1L, "test@example.com", 100L,
            "Dr. Smith", "2026-06-01", "10:00"
        );

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ── sendAppointmentCancelled ──

    @Test
    void sendAppointmentCancelled_Success() throws Exception {
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.sendAppointmentCancelled(
            1L, "test@example.com", 100L,
            "Dr. Smith", "2026-06-01"
        );

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ── sendPaymentSuccess ──

    @Test
    void sendPaymentSuccess_Success() throws Exception {
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.sendPaymentSuccess(
            1L, "test@example.com", "500.0", "TXN123"
        );

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ── sendPaymentRefunded ──

    @Test
    void sendPaymentRefunded_Success() throws Exception {
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.sendPaymentRefunded(
            1L, "test@example.com", "500.0", "TXN123"
        );

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // ── getByUserId ──

    @Test
    void getByUserId_ReturnsList() {
        when(notificationRepository.findByUserId(1L))
            .thenReturn(Arrays.asList(notification));

        List<Notification> results = notificationService.getByUserId(1L);

        assertEquals(1, results.size());
    }

    // ── getUnreadByUserId ──

    @Test
    void getUnreadByUserId_ReturnsUnread() {
        when(notificationRepository.findByUserIdAndIsRead(1L, false))
            .thenReturn(Arrays.asList(notification));

        List<Notification> results = notificationService.getUnreadByUserId(1L);

        assertEquals(1, results.size());
        assertFalse(results.get(0).getIsRead());
    }

    // ── getUnreadCount ──

    @Test
    void getUnreadCount_ReturnsCount() {
        when(notificationRepository.countByUserIdAndIsRead(1L, false)).thenReturn(3L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(3L, count);
    }

    // ── markAsRead ──

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.markAsRead(1L);

        assertTrue(result.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_NotFound_ThrowsException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> notificationService.markAsRead(99L));
    }

    // ── deleteNotification ──

    @Test
    void deleteNotification_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(notification);

        assertDoesNotThrow(() -> notificationService.deleteNotification(1L));
        verify(notificationRepository, times(1)).delete(notification);
    }
}
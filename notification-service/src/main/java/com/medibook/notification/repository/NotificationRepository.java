package com.medibook.notification.repository;

import com.medibook.notification.entity.Notification;
import com.medibook.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserId(Long userId);

	List<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead);

	List<Notification> findByUserIdAndType(Long userId, NotificationType type);

	long countByUserIdAndIsRead(Long userId, Boolean isRead);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.isRead = true " + "WHERE n.userId = :userId")
	void markAllAsRead(@Param("userId") Long userId);
}
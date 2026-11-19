
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.NotificationResponse;
import com.PeerNest.PeerNest.Entity.Notification;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.NotificationRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(
            Long recipientId,
            String title,
            String message,
            String type) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new RuntimeException("Notification recipient not found"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String email) {

        User user = getUser(email);

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String email) {

        User user = getUser(email);

        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
                        user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public long getUnreadCount(String email) {

        User user = getUser(email);

        return notificationRepository
                .countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            String email) {

        User user = getUser(email);

        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        notification.setIsRead(true);

        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {

        User user = getUser(email);

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
                                user.getId());

        notifications.forEach(notification ->
                notification.setIsRead(true));

        notificationRepository.saveAll(notifications);
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

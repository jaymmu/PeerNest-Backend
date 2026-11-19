
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void createNotification(
            Long recipientId,
            String title,
            String message,
            String type
    );

    List<NotificationResponse> getMyNotifications(String email);

    List<NotificationResponse> getUnreadNotifications(String email);

    long getUnreadCount(String email);

    NotificationResponse markAsRead(Long notificationId, String email);

    void markAllAsRead(String email);
}

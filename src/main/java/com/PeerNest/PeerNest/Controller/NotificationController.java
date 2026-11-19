
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.NotificationResponse;
import com.PeerNest.PeerNest.Service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my-notifications")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(
                        authentication.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            Authentication authentication) {

        return ResponseEntity.ok(
                Map.of("unreadCount",
                        notificationService.getUnreadCount(
                                authentication.getName())));
    }

    @PutMapping("/mark-read/{notificationId}")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        authentication.getName()));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<String> markAllAsRead(
            Authentication authentication) {

        notificationService.markAllAsRead(authentication.getName());

        return ResponseEntity.ok(
                "All notifications marked as read");
    }
}

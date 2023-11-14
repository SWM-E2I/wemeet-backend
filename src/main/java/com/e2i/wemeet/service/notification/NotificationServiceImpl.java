package com.e2i.wemeet.service.notification;

import com.e2i.wemeet.domain.notification.PushTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationHandleService notificationHandleService;
    private final PushTokenRepository pushTokenRepository;

    @Override
    public void sendToAllMembers(String title, String body) {
        List<String> tokens = pushTokenRepository.findAllMemberTokens();
        notificationHandleService.sendPushNotification(tokens, title, body);
    }

    @Override
    public void sendToMembersWithoutTeam(String title, String body) {
        List<String> tokens = pushTokenRepository.findTokensOfMemberWithoutTeam();
        notificationHandleService.sendPushNotification(tokens, title, body);
    }
}

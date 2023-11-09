package com.e2i.wemeet.service.notification;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.e2i.wemeet.domain.notification.PushTokenRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationHandleService notificationHandleService;

    @Mock
    private PushTokenRepository pushTokenRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private final List<String> tokens = List.of("token1", "token2", "token3");
    private final String title = "Test Title";
    private final String body = "Test Body";


    @DisplayName("모든 사용자에게 푸시 알림을 보낼 수 있다.")
    @Test
    void sendToAllMembers_shouldCallNotificationHandleServiceWithCorrectParameters() {
        // given
        given(pushTokenRepository.findAllMemberTokens()).willReturn(tokens);

        // when
        notificationService.sendToAllMembers(title, body);

        // then
        verify(notificationHandleService).sendPushNotification(tokens, title, body);
    }

    @DisplayName("팀이 없는 사용자들에게 푸시 알림을 보낼 수 있다.")
    @Test
    void sendToMembersWithoutTeam_shouldCallNotificationHandleServiceWithCorrectParameters() {
        // given
        given(pushTokenRepository.findTokensOfMemberWithoutTeam()).willReturn(tokens);

        // when
        notificationService.sendToMembersWithoutTeam(title, body);

        // then
        verify(notificationHandleService).sendPushNotification(tokens, title, body);
    }
}
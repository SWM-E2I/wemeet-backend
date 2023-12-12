package com.e2i.wemeet.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.PushClient;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationHandleServiceTest {

    @Mock
    private PushClient mockPushClient;

    @InjectMocks
    private NotificationHandleServiceImpl notificationHandleService;

    @Captor
    ArgumentCaptor<List<ExpoPushMessage>> messageCaptor;


    @DisplayName("푸시 알림을 전송할 수 있다.")
    @Test
    void sendPushNotificationWithBody() {
        // given
        List<String> tokens = List.of("token1", "token2");
        String title = "Test Title";
        String body = "Test Body";

        // when
        notificationHandleService.sendPushNotification(tokens, title, body);

        // then
        verify(mockPushClient).sendPushNotificationsAsync(messageCaptor.capture());

        List<ExpoPushMessage> capturedMessages = messageCaptor.getValue();
        assertThat(capturedMessages).hasSize(1);
        ExpoPushMessage capturedMessage = capturedMessages.get(0);

        assertThat(capturedMessage.getTo()).isEqualTo(tokens);
        assertThat(capturedMessage.getTitle()).isEqualTo(title);
        assertThat(capturedMessage.getBody()).isEqualTo(body);
    }

    @DisplayName("Body가 없는 푸시 알림을 전송할 수 있다.")
    @Test
    void sendPushNotificationWithoutBody() {
        // given
        List<String> tokens = List.of("token1", "token2");
        String title = "Test Title";

        // when
        notificationHandleService.sendPushNotification(tokens, title);

        // then
        verify(mockPushClient).sendPushNotificationsAsync(messageCaptor.capture());

        List<ExpoPushMessage> capturedMessages = messageCaptor.getValue();
        assertThat(capturedMessages).hasSize(1);
        ExpoPushMessage capturedMessage = capturedMessages.get(0);

        assertThat(capturedMessage.getTo()).isEqualTo(tokens);
        assertThat(capturedMessage.getTitle()).isEqualTo(title);
        assertThat(capturedMessage.getBody()).isNull();
    }
}

package com.e2i.wemeet.service.notification;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    private static final String TITLE = "기다리고 기다리던 11시 11분이야!";
    private static final String BODY = "오늘의 추천 친구들을 확인해 봐! 🤩";

    @DisplayName("sendPushNotificationForSuggestion 메소드가 실행되면 sendToAllMembers 메소드가 실행된다.")
    @Test
    void sendPushNotificationForSuggestionTest() {
        // when
        notificationScheduler.sendPushNotificationForSuggestion();

        // then
        verify(notificationService).sendToAllMembers(TITLE, BODY);
    }
}

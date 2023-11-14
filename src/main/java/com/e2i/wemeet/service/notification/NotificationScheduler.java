package com.e2i.wemeet.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationScheduler {

    private final NotificationService notificationService;
    private static final String TITLE = "기다리고 기다리던 11시 11분이야!";
    private static final String BODY = "오늘의 추천 친구들을 확인해 봐! 🤩";

    // 매일 23시 11분에 실행
    @Scheduled(cron = "0 11 23 * * ?")
    public void sendPushNotificationForSuggestion() {
        notificationService.sendToAllMembers(TITLE, BODY);
    }
}



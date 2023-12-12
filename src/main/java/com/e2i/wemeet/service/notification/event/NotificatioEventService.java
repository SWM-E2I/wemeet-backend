package com.e2i.wemeet.service.notification.event;

public interface NotificatioEventService {

    /*
     * 이벤트 전용 푸시 알림 전송
     */
    void sendForNotificationEvent(NotificationEvent notificationEvent);
}

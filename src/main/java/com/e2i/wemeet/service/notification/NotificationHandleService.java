package com.e2i.wemeet.service.notification;


import java.util.List;

public interface NotificationHandleService {

    /*
     * Body 미포함 푸시 알림 전송
     */
    void sendPushNotification(List<String> tokens, String title);

    /*
     * Body 포함 푸시 알림 전송
     */
    void sendPushNotification(List<String> tokens, String title, String body);

}

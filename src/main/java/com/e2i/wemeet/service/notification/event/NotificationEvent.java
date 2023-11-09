package com.e2i.wemeet.service.notification.event;

public record NotificationEvent(String token, String title) {

    public static NotificationEvent of(String token, String title) {
        return new NotificationEvent(token, title);
    }
}

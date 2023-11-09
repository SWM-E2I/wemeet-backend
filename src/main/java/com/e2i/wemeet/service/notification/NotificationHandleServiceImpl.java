package com.e2i.wemeet.service.notification;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.PushClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class NotificationHandleServiceImpl implements NotificationHandleService {

    private final PushClient client;

    @Override
    public void sendPushNotification(List<String> tokens, String title) {
        sendPushNotification(tokens, title, null);
    }

    @Override
    public void sendPushNotification(List<String> tokens, String title, String body) {
        ExpoPushMessage expoPushMessage = createExpoPushMessage(tokens, title, body);
        client.sendPushNotificationsAsync(List.of(expoPushMessage));
    }

    private ExpoPushMessage createExpoPushMessage(List<String> tokens, String title, String body) {
        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.setTo(tokens);
        expoPushMessage.setTitle(title);

        if (body != null && !body.isBlank()) {
            expoPushMessage.setBody(body);
        }

        return expoPushMessage;
    }
}

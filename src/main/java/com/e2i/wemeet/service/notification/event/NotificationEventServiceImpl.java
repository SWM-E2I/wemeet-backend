package com.e2i.wemeet.service.notification.event;

import com.e2i.wemeet.service.meeting.MeetingEvent;
import com.e2i.wemeet.service.notification.NotificationHandleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationEventServiceImpl implements NotificatioEventService {

    private final NotificationHandleService notificationHandleService;

    @EventListener(classes = MeetingEvent.class)
    public void sendForNotificationEvent(final MeetingEvent event) {
        sendForNotificationEvent(event.notificationEvent());
    }

    @EventListener(classes = NotificationEvent.class)
    @Override
    public void sendForNotificationEvent(final NotificationEvent notificationEvent) {
        if (notificationEvent.token() == null) {
            return;
        }

        notificationHandleService.sendPushNotification(List.of(notificationEvent.token()),
            notificationEvent.title());
    }
}

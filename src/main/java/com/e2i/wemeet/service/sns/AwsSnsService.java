package com.e2i.wemeet.service.sns;

import static com.e2i.wemeet.exception.ErrorCode.AWS_SNS_MESSAGE_TRANSFER_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.service.meeting.MeetingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSnsService implements SnsService {

    private final SnsClient snsClient;

    @EventListener(classes = MeetingEvent.class)
    public void issue(final MeetingEvent event) {
        issue(event.snsEvent());
    }

    @EventListener(classes = SnsEvent.class)
    @Override
    public void issue(final SnsEvent snsEvent) {
        try {
            PublishRequest request = PublishRequest.builder()
                .message(snsEvent.message())
                .phoneNumber(snsEvent.receivePhoneNumber())
                .build();

            PublishResponse result = snsClient.publish(request);
            log.info("SNS Publish Success:: phoneNumber: {} / message: {} / publishId: {}",
                snsEvent.receivePhoneNumber(), snsEvent.message(), result.messageId());

        } catch (SnsException e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_SNS_MESSAGE_TRANSFER_ERROR);
        }
    }

}

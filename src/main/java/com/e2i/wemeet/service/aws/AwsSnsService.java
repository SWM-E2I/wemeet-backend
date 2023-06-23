package com.e2i.wemeet.service.aws;

import static com.e2i.wemeet.exception.ErrorCode.AWS_SNS_MESSAGE_TRANSFER_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSnsService {

    private final CredentialService credentialService;

    public void sendSms(String phoneNumber, String message) {
        SnsClient snsClient = credentialService.getSnsClient();
        try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .build();

            PublishResponse result = snsClient.publish(request);
            log.info("Message sent. MessageId: " + result.messageId());

        } catch (SnsException e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_SNS_MESSAGE_TRANSFER_ERROR);
        }
    }
}

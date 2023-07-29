package com.e2i.wemeet.service.aws.sns;

import static com.e2i.wemeet.exception.ErrorCode.AWS_SNS_MESSAGE_TRANSFER_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.SmsCredentialNotFoundException;
import com.e2i.wemeet.service.credential.sms.SmsCredentialService;
import com.e2i.wemeet.util.RandomCodeUtils;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSnsService implements SmsCredentialService {

    private final SnsClient snsClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void issue(String receiveTarget) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String credential = RandomCodeUtils.crateCredential();
        sendSms(receiveTarget, credential);

        operations.set(receiveTarget, credential, Duration.ofMinutes(10));
    }

    @Override
    public boolean matches(String target, String input) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String origin = operations.get(target);

        if (!StringUtils.hasText(origin)) {
            throw new SmsCredentialNotFoundException();
        }

        return origin.equals(input);
    }


    private void sendSms(String phoneNumber, String message) {
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

package com.e2i.wemeet.service.aws.ses;

import static com.e2i.wemeet.exception.ErrorCode.AWS_SES_EMAIL_TRANSFER_ERROR;
import static com.e2i.wemeet.exception.ErrorCode.MAIL_JSON_PARSING_ERROR;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.badrequest.DuplicatedMailException;
import com.e2i.wemeet.exception.internal.InternalServerException;
import com.e2i.wemeet.exception.notfound.SmsCredentialNotFoundException;
import com.e2i.wemeet.service.credential.email.EmailCredentialService;
import com.e2i.wemeet.util.RandomCodeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSesService implements EmailCredentialService {

    private final SesClient sesClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    @Value("${aws.ses.templateName}")
    private String templateName;


    @Value("${aws.ses.sourceEmail}")
    private String sourceEmail;

    @Override
    public void issue(String receiveTarget) {
        validateIsExistMail(receiveTarget);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        String credential = RandomCodeUtils.crateCredential();
        sendEmail(receiveTarget, credential);

        operations.set(receiveTarget, credential, Duration.ofMinutes(10));
    }

    @Override
    @Transactional
    public boolean matches(String target, String input, Long memberId) {
        validateIsExistMail(target);

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String origin = operations.get(target);
        if (!StringUtils.hasText(origin)) {
            throw new SmsCredentialNotFoundException();
        }

        boolean result = origin.equals(input);
        if (result) {
            memberRepository.findByMemberId(memberId).ifPresent(member ->
                member.saveEmail(target));
        }

        return result;
    }


    private void sendEmail(String email, String message) {
        SendTemplatedEmailRequest emailRequest = createEmailRequest(email, message);

        try {
            SendTemplatedEmailResponse result = sesClient.sendTemplatedEmail(emailRequest);
            log.info("Email sent. EmailId: " + result.messageId());
        } catch (SesException e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_SES_EMAIL_TRANSFER_ERROR);
        }
    }

    private void validateIsExistMail(String mail) {
        memberRepository.findByEmail(mail)
            .ifPresent(member -> {
                throw new DuplicatedMailException();
            });
    }

    private SendTemplatedEmailRequest createEmailRequest(String email, String message) {
        try {
            return SendTemplatedEmailRequest.builder()
                .source(sourceEmail)
                .destination(d -> d.toAddresses(email))
                .template(templateName)
                .templateData(this.toJson(message))
                .build();
        } catch (JsonProcessingException e) {
            throw new InternalServerException(MAIL_JSON_PARSING_ERROR);
        }
    }

    private String toJson(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> templateData = new HashMap<>();
        templateData.put("code", message);

        return mapper.writeValueAsString(templateData);
    }
}

package com.e2i.wemeet.service.aws.ses;

import static com.e2i.wemeet.exception.ErrorCode.AWS_SES_EMAIL_TRANSFER_ERROR;

import com.e2i.wemeet.exception.internal.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsSesService {

    private final AwsSesCredentialService awsSesCredentialService;
    private static final String TEMPLATE_NAME = "certifyEmailTemplate";

    private static final String SOURCE_EMAIL = "qkrdbsk28@naver.com";

    public void sendEmail(String email, String message) throws JsonProcessingException {
        SesClient sesClient = awsSesCredentialService.getSesClient();

        SendTemplatedEmailRequest emailRequest = SendTemplatedEmailRequest.builder()
            .source(SOURCE_EMAIL)
            .destination(d -> d.toAddresses(email))
            .template(TEMPLATE_NAME)
            .templateData(this.toJson(message))
            .build();

        try {
            SendTemplatedEmailResponse result = sesClient.sendTemplatedEmail(emailRequest);
            log.info("Email sent. EmailId: " + result.messageId());

        } catch (SesException e) {
            log.info(e.getMessage());
            throw new InternalServerException(AWS_SES_EMAIL_TRANSFER_ERROR);
        }
    }

    private String toJson(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> templateData = new HashMap<>();
        templateData.put("code", message);

        return mapper.writeValueAsString(templateData);
    }
}

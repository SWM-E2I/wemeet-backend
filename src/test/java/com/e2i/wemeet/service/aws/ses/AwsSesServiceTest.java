package com.e2i.wemeet.service.aws.ses;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.exception.internal.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@ExtendWith(MockitoExtension.class)
class AwsSesServiceTest {

    @InjectMocks
    private AwsSesService awsSesService;

    @Mock
    private AwsSesCredentialService awsSesCredentialService;

    @Mock
    private SesClient sesClient;

    private static final String email = "test@test.com";
    private static final String message = "Test message";
    
    @Test
    void testSendEmail() throws JsonProcessingException {
        when(awsSesCredentialService.getSesClient()).thenReturn(sesClient);
        SendTemplatedEmailResponse sendEmailResponse = SendTemplatedEmailResponse.builder()
            .messageId("message-id")
            .build();
        when(sesClient.sendTemplatedEmail(any(SendTemplatedEmailRequest.class))).thenReturn(
            sendEmailResponse);

        awsSesService.sendEmail(email, message);

        verify(sesClient).sendTemplatedEmail(any(SendTemplatedEmailRequest.class));
    }

    @Test
    void testSendEmail_Failure() throws JsonProcessingException {
        when(awsSesCredentialService.getSesClient()).thenReturn(sesClient);
        when(sesClient.sendTemplatedEmail(any(SendTemplatedEmailRequest.class))).thenThrow(
            SesException.class);

        assertThatThrownBy(() -> awsSesService.sendEmail(email, message))
            .isExactlyInstanceOf(InternalServerException.class);
    }
}

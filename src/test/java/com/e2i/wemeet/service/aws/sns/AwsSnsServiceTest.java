package com.e2i.wemeet.service.aws.sns;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.exception.internal.InternalServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@ExtendWith(MockitoExtension.class)
class AwsSnsServiceTest {

    @InjectMocks
    private AwsSnsService awsSnsService;

    @Mock
    private AwsSnsCredentialService awsSnsCredentialService;

    @Mock
    private SnsClient snsClient;

    private static final String phoneNumber = "1234567890";
    private static final String message = "Test message";

    @Test
    void testSendSms() {
        when(awsSnsCredentialService.getSnsClient()).thenReturn(snsClient);
        PublishResponse publishResponse = PublishResponse.builder().build();
        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        awsSnsService.sendSms(phoneNumber, message);

        verify(snsClient).publish(PublishRequest.builder()
            .message(message)
            .phoneNumber(phoneNumber)
            .build());
    }

    @Test
    void testSendSms_Failure() {
        when(awsSnsCredentialService.getSnsClient()).thenReturn(snsClient);
        when(snsClient.publish(any(PublishRequest.class))).thenThrow(SnsException.class);

        assertThatThrownBy(() -> awsSnsService.sendSms(phoneNumber, message))
            .isExactlyInstanceOf(InternalServerException.class);
    }
}

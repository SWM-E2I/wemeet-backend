package com.e2i.wemeet.service.aws;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.exception.internal.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

class AwsSnsServiceTest {

    private AwsSnsService awsSnsService;

    @Mock
    private AwsCredentialService awsCredentialService;

    @Mock
    private SnsClient snsClient;

    private static final String phoneNumber = "1234567890";
    private static final String message = "Test message";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        awsSnsService = new AwsSnsService(awsCredentialService);
    }

    @Test
    void testSendSms() {
        when(awsCredentialService.getSnsClient()).thenReturn(snsClient);
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
        when(awsCredentialService.getSnsClient()).thenReturn(snsClient);
        when(snsClient.publish(any(PublishRequest.class))).thenThrow(SnsException.class);

        assertThrows(InternalServerException.class,
            () -> awsSnsService.sendSms(phoneNumber, message));
    }
}

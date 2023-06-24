package com.e2i.wemeet.service.aws;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

class AwsSnsServiceTest {

    private AwsSnsService awsSnsService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private SnsClient snsClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        awsSnsService = new AwsSnsService(credentialService);
    }

    @Test
    void testSendSms() {
        when(credentialService.getSnsClient()).thenReturn(snsClient);
        PublishResponse publishResponse = PublishResponse.builder().build();
        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        awsSnsService.sendSms("1234567890", "test message");

        verify(snsClient).publish(PublishRequest.builder()
            .message("test message")
            .phoneNumber("1234567890")
            .build());
    }
}

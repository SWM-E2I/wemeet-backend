package com.e2i.wemeet.service.aws.sns;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.aws.AwsSnsConfig;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sns.SnsClient;

@ExtendWith(MockitoExtension.class)
class AwsSnsCredentialServiceTest {

    @Mock
    private AwsSnsConfig awsSnsConfig;
    @Mock
    private AwsCredentialService awsCredentialService;

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";
    private static final String region = "your-region";

    @Test
    void testGetSnsClient() {
        AwsSnsCredentialService awsSnsCredentialService = new AwsSnsCredentialService(awsSnsConfig,
            awsCredentialService);
        when(awsSnsConfig.getAccessKey()).thenReturn(accessKeyId);
        when(awsSnsConfig.getSecretKey()).thenReturn(secretAccessKey);
        when(awsSnsConfig.getRegion()).thenReturn(region);

        SnsClient snsClient = awsSnsCredentialService.getSnsClient();

        assertNotNull(snsClient);
    }
}

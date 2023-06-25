package com.e2i.wemeet.service.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.aws.AwsSnsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

@ExtendWith(MockitoExtension.class)
class AwsCredentialServiceTest {

    @Mock
    private AwsSnsConfig awsSnsConfig;

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";

    @Test
    void testGetAwsCredentials() {
        AwsCredentialService awsCredentialService = new AwsCredentialService(awsSnsConfig);
        AwsCredentialsProvider credentialsProvider = awsCredentialService.getAwsCredentials(
            accessKeyId, secretAccessKey);

        assertNotNull(credentialsProvider);
        assertEquals(accessKeyId, credentialsProvider.resolveCredentials().accessKeyId());
        assertEquals(secretAccessKey, credentialsProvider.resolveCredentials().secretAccessKey());
    }

    @Test
    void testGetSnsClient() {
        String region = "your-region";

        when(awsSnsConfig.getAccessKey()).thenReturn(accessKeyId);
        when(awsSnsConfig.getSecretKey()).thenReturn(secretAccessKey);
        when(awsSnsConfig.getRegion()).thenReturn(region);

        AwsCredentialService awsCredentialService = new AwsCredentialService(awsSnsConfig);
        SnsClient snsClient = awsCredentialService.getSnsClient();

        assertNotNull(snsClient);
    }
}

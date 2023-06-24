package com.e2i.wemeet.service.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.aws.AwsSnsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;

class CredentialServiceTest {

    @Mock
    private AwsSnsConfig awsSnsConfig;

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAwsCredentials() {
        CredentialService credentialService = new CredentialService(awsSnsConfig);
        AwsCredentialsProvider credentialsProvider = credentialService.getAwsCredentials(
            accessKeyId, secretAccessKey);

        assertNotNull(credentialsProvider);
        assertEquals(accessKeyId, credentialsProvider.resolveCredentials().accessKeyId());
        assertEquals(secretAccessKey, credentialsProvider.resolveCredentials().secretAccessKey());
    }

    @Test
    void testGetSnsClient() {
        String region = "your-region";

        when(awsSnsConfig.getAwsAccessKey()).thenReturn(accessKeyId);
        when(awsSnsConfig.getAwsSecretKey()).thenReturn(secretAccessKey);
        when(awsSnsConfig.getAwsRegion()).thenReturn(region);

        CredentialService credentialService = new CredentialService(awsSnsConfig);
        SnsClient snsClient = credentialService.getSnsClient();

        assertNotNull(snsClient);
    }
}

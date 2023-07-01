package com.e2i.wemeet.service.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

class AwsCredentialServiceTest {

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";

    @Test
    void testGetAwsCredentials() {
        AwsCredentialService awsCredentialService = new AwsCredentialService();
        AwsCredentialsProvider credentialsProvider = awsCredentialService.getAwsCredentials(
            accessKeyId, secretAccessKey);

        assertNotNull(credentialsProvider);
        assertEquals(accessKeyId, credentialsProvider.resolveCredentials().accessKeyId());
        assertEquals(secretAccessKey, credentialsProvider.resolveCredentials().secretAccessKey());
    }
}

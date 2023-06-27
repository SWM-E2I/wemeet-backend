package com.e2i.wemeet.service.aws.s3;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.aws.AwsS3Config;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;

@ExtendWith(MockitoExtension.class)
class AwsS3CredentialServiceTest {

    @Mock
    private AwsS3Config awsS3Config;
    @Mock
    private AwsCredentialService awsCredentialService;

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";
    private static final String region = "your-region";

    @Test
    void testGetS3Client() {
        AwsS3CredentialService awsS3CredentialService = new AwsS3CredentialService(awsS3Config,
            awsCredentialService);
        when(awsS3Config.getAccessKey()).thenReturn(accessKeyId);
        when(awsS3Config.getSecretKey()).thenReturn(secretAccessKey);
        when(awsS3Config.getRegion()).thenReturn(region);

        S3Client s3Client = awsS3CredentialService.getS3Client();

        assertNotNull(s3Client);
    }
}

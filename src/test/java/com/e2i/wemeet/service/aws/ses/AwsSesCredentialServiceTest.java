package com.e2i.wemeet.service.aws.ses;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.aws.AwsSesConfig;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ses.SesClient;

@ExtendWith(MockitoExtension.class)
class AwsSesCredentialServiceTest {

    @Mock
    private AwsSesConfig awsSesConfig;
    @Mock
    private AwsCredentialService awsCredentialService;

    private static final String accessKeyId = "your-access-key-id";
    private static final String secretAccessKey = "your-secret-access-key";
    private static final String region = "your-region";

    @Test
    void testGetSesClient() {
        AwsSesCredentialService awsSesCredentialService = new AwsSesCredentialService(awsSesConfig,
            awsCredentialService);
        when(awsSesConfig.getAccessKey()).thenReturn(accessKeyId);
        when(awsSesConfig.getSecretKey()).thenReturn(secretAccessKey);
        when(awsSesConfig.getRegion()).thenReturn(region);

        SesClient sesClient = awsSesCredentialService.getSesClient();

        assertNotNull(sesClient);
    }
}

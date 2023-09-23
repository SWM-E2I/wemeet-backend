package com.e2i.wemeet.service.aws.ses;

import com.e2i.wemeet.config.aws.AwsSesConfig;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@RequiredArgsConstructor
@Service
public class AwsSesCredentialService {

    private final AwsSesConfig awsSesConfig;
    private final AwsCredentialService awsCredentialService;

    public SesClient getSesClient() {
        return SesClient.builder()
            .credentialsProvider(
                awsCredentialService.getAwsCredentials(awsSesConfig.getAccessKey(),
                    awsSesConfig.getSecretKey())
            ).region(Region.of(awsSesConfig.getRegion()))
            .build();
    }
}

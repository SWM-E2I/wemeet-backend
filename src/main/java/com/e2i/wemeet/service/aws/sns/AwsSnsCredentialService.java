package com.e2i.wemeet.service.aws.sns;

import com.e2i.wemeet.config.aws.AwsSnsConfig;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@RequiredArgsConstructor
@Service
public class AwsSnsCredentialService {

    private final AwsSnsConfig awsSnsConfig;
    private final AwsCredentialService awsCredentialService;

    public SnsClient getSnsClient() {
        return SnsClient.builder()
            .credentialsProvider(
                awsCredentialService.getAwsCredentials(awsSnsConfig.getAccessKey(),
                    awsSnsConfig.getSecretKey())
            ).region(Region.of(awsSnsConfig.getRegion()))
            .build();
    }
}

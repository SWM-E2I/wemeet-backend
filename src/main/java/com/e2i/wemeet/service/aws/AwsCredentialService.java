package com.e2i.wemeet.service.aws;

import com.e2i.wemeet.config.aws.AwsSnsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@RequiredArgsConstructor
@Service
public class AwsCredentialService {

    private final AwsSnsConfig awsSnsConfig;

    public AwsCredentialsProvider getAwsCredentials(String accessKeyId, String secretAccessKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyId,
            secretAccessKey);
        return () -> awsBasicCredentials;
    }

    public SnsClient getSnsClient() {
        return SnsClient.builder()
            .credentialsProvider(
                getAwsCredentials(awsSnsConfig.getAwsAccessKey(), awsSnsConfig.getAwsSecretKey())
            ).region(Region.of(awsSnsConfig.getAwsRegion()))
            .build();
    }
}

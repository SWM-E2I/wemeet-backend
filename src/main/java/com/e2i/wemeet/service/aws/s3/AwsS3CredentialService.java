package com.e2i.wemeet.service.aws.s3;

import com.e2i.wemeet.config.aws.AwsS3Config;
import com.e2i.wemeet.service.aws.AwsCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Service
public class AwsS3CredentialService {

    private final AwsS3Config awsS3Config;
    private final AwsCredentialService awsCredentialService;

    public S3Client getS3Client() {
        return S3Client.builder()
            .credentialsProvider(
                awsCredentialService.getAwsCredentials(awsS3Config.getAccessKey(),
                    awsS3Config.getSecretKey())
            ).region(Region.of(awsS3Config.getRegion()))
            .build();
    }
}

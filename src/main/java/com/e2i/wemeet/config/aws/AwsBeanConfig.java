package com.e2i.wemeet.config.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

@RequiredArgsConstructor
@Configuration
public class AwsBeanConfig {

    private final AwsSesConfig awsSesConfig;
    private final AwsSnsConfig awsSnsConfig;
    private final AwsS3Config awsS3Config;

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
            .credentialsProvider(
                getAwsCredentials(awsSesConfig.getAccessKey(),
                    awsSesConfig.getSecretKey())
            ).region(Region.of(awsSesConfig.getRegion()))
            .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
            .credentialsProvider(
                getAwsCredentials(
                    awsSnsConfig.getAccessKey(),
                    awsSnsConfig.getSecretKey())
            ).region(Region.of(awsSnsConfig.getRegion()))
            .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .credentialsProvider(
                getAwsCredentials(awsS3Config.getAccessKey(),
                    awsS3Config.getSecretKey())
            ).region(Region.of(awsS3Config.getRegion()))
            .build();
    }

    private AwsCredentialsProvider getAwsCredentials(final String accessKeyId,
        final String secretAccessKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKeyId,
            secretAccessKey);
        return () -> awsBasicCredentials;
    }
}

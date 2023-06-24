package com.e2i.wemeet.config.aws;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AwsSnsConfig {

    @Value("${aws.sns.accessKey}")
    private String awsAccessKey;

    @Value("${aws.sns.secretKey}")
    private String awsSecretKey;

    @Value("${aws.sns.region}")
    private String awsRegion;
}

package com.e2i.wemeet.config.aws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("aws.ses")
public class AwsSesConfig {

    private final String accessKey;

    private final String secretKey;

    private final String region;
}

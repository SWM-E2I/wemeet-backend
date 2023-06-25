package com.e2i.wemeet.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("aws.sns")
@Configuration
public class AwsSnsConfig {

    private String accessKey;

    private String secretKey;

    private String region;
}

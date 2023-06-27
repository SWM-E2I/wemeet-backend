package com.e2i.wemeet.config;

import com.e2i.wemeet.config.aws.AwsS3Config;
import com.e2i.wemeet.config.aws.AwsSesConfig;
import com.e2i.wemeet.config.aws.AwsSnsConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({AwsSesConfig.class, AwsSnsConfig.class, AwsS3Config.class})
@Configuration
public class WebConfig {

}

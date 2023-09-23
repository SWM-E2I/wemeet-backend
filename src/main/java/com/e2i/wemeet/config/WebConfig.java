package com.e2i.wemeet.config;

import com.e2i.wemeet.config.aws.AwsS3Config;
import com.e2i.wemeet.config.aws.AwsSesConfig;
import com.e2i.wemeet.config.aws.AwsSnsConfig;
import com.e2i.wemeet.config.resolver.member.MemberIdArgumentResolver;
import com.e2i.wemeet.util.encryption.AdvancedEncryptionStandard;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties({
    AwsSesConfig.class, AwsSnsConfig.class, AwsS3Config.class,
    AdvancedEncryptionStandard.class
})
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberIdArgumentResolver());
    }
}

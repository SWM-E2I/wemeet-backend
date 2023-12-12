package com.e2i.wemeet.config.notification;

import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public PushClient pushClient() throws PushClientException {
        return new PushClient();
    }
}
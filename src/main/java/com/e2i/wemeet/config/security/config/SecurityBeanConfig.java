package com.e2i.wemeet.config.security.config;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

@Configuration
public class SecurityBeanConfig {
    @Bean
    public AuthenticationExceptionFilter authenticationExceptionFilter(ObjectMapper objectMapper, MessageSourceAccessor accessor) {
        return new AuthenticationExceptionFilter(objectMapper, accessor);
    }
}

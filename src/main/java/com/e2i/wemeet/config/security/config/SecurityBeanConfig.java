package com.e2i.wemeet.config.security.config;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.config.security.filter.LoginAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.ArrayList;

@Configuration
public class SecurityBeanConfig {
    // 인증 과정에서 발생하는 예외를 처리
    @Bean
    public AuthenticationExceptionFilter authenticationExceptionFilter(ObjectMapper objectMapper, MessageSourceAccessor accessor) {
        return new AuthenticationExceptionFilter(objectMapper, accessor);
    }

    // 로그인 인증 처리 필터
    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter(AuthenticationManager manager, ObjectMapper objectMapper) {
        return new LoginAuthenticationFilter(manager, objectMapper);
    }

    // Authentication Manager -> Provider 에 로그인 요청을 전달
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        ProviderManager providerManager = new ProviderManager(new ArrayList<>(), authenticationManager);

        return providerManager;
    }

    // 로그인 정보가 유효한지 확인 후 인증 객체 전달
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//
//    }
}

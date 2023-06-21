package com.e2i.wemeet.config.security.config;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationExceptionFilter authenticationExceptionFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 미사용 API disable
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .headers(header -> header.frameOptions(
                        (HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                ))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // URL 인가 정책 적용
        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/v1/member").permitAll()
                                .requestMatchers("/v1/auth/phone/*").permitAll()
                                .anyRequest().authenticated()
                );

        // 커스텀 필터 적용
        http
                .addFilterAfter(authenticationExceptionFilter, LogoutFilter.class);

        return http.build();
    }
}

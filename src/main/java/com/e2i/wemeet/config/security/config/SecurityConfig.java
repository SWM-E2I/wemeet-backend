package com.e2i.wemeet.config.security.config;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.config.security.filter.LoginAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import static org.springframework.http.HttpMethod.POST;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationExceptionFilter authenticationExceptionFilter;
    private final LoginAuthenticationFilter loginAuthenticationFilter;


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

        /* URL 인가 정책 적용
        * 허용 목록
        * - 회원 가입
        * - 인증 번호 발송, 인증 번호 확인
         */
        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(POST, "/v1/member", "/v1/auth/phone/*").permitAll()
                                .anyRequest().authenticated()
                );

        // 커스텀 필터 적용
        http
                .addFilterAfter(authenticationExceptionFilter, LogoutFilter.class)
                .addFilterAfter(loginAuthenticationFilter, AuthenticationExceptionFilter.class);

        return http.build();
    }
}

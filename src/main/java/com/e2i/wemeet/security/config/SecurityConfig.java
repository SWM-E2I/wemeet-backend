package com.e2i.wemeet.security.config;

import static org.springframework.http.HttpMethod.POST;

import com.e2i.wemeet.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.security.filter.JwtAuthenticationFilter;
import com.e2i.wemeet.security.filter.RefreshTokenProcessingFilter;
import com.e2i.wemeet.security.filter.RequestEndPointCheckFilter;
import com.e2i.wemeet.security.filter.SmsLoginProcessingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationExceptionFilter authenticationExceptionFilter;
    private final SmsLoginProcessingFilter SMSLoginProcessingFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RefreshTokenProcessingFilter refreshTokenProcessingFilter;
    private final RequestEndPointCheckFilter requestEndPointCheckFilter;

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

        // Spring Security 의 Session 기반 인증 처리 비활성화
        http
            .sessionManagement(
                sessionConfigurer -> sessionConfigurer
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .disable());

        // Custom filter 로 예외처리하기 위해 ExceptionTranslationFilter 비활성화
        http
            .exceptionHandling(
                AbstractHttpConfigurer::disable);

        /* URL 인가 정책 적용
         * 허용 목록
         * - 회원 가입
         * - 인증 번호 발송, 인증 번호 확인
         * 나머지 요청
         * ->
         */
        http
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers(POST, "/v1/member", "/v1/auth/phone/**", "/v1/auth/refresh").permitAll()
                    .requestMatchers("/test/**", "/health**", "/h2-console",
                        "/static/**", "/api-docs"
                    ).permitAll()
                    .anyRequest().authenticated());


        /* 커스텀 필터 적용
         * 1. AuthenticationExceptionFilter : Custom Filter 에서 발생하는 예외를 처리
         * 2. LoginProcessingFilter : 사용자의 로그인 요청을 처리 (SMS 인증을 통해, Access, Refresh Token 발급)
         * 3. RefreshTokenProcessingFilter : RefreshToken 재발급 요청 수행
         * 4. RequestEndPointCheckFilter : 요청의 EndPoint 가 존재하는지 검사
         * 5. JwtAuthenticationFiler : AccessToken 의 유효성을 검사 후, SecurityContext 에 인증 객체 할당
         */
        http
            .addFilterAfter(authenticationExceptionFilter, LogoutFilter.class)
            .addFilterAfter(SMSLoginProcessingFilter, AuthenticationExceptionFilter.class)
            .addFilterAfter(refreshTokenProcessingFilter, SmsLoginProcessingFilter.class)
            .addFilterAfter(requestEndPointCheckFilter, RefreshTokenProcessingFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, ExceptionTranslationFilter.class);

        return http.build();
    }
}

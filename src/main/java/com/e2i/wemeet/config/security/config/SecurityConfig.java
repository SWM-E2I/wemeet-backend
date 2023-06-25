package com.e2i.wemeet.config.security.config;

import static org.springframework.http.HttpMethod.POST;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.config.security.filter.JwtAuthenticationFilter;
import com.e2i.wemeet.config.security.filter.LoginProcessingFilter;
import com.e2i.wemeet.config.security.filter.RefreshTokenProcessingFilter;
import com.e2i.wemeet.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationExceptionFilter authenticationExceptionFilter;
    private final LoginProcessingFilter loginProcessingFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RefreshTokenProcessingFilter refreshTokenProcessingFilter;

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
                        .sessionFixation().none()
                        .disable());


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
                                .requestMatchers(POST, "/v1/member", "/v1/auth/phone/**", "/v1/auth/refresh",
                                    "/test**").permitAll()
                                .requestMatchers("/v1/member/**").hasRole(Role.USER.name())
                                .anyRequest().authenticated());


        /* 커스텀 필터 적용
        * 1. AuthenticationExceptionFilter : Custom Filter 에서 발생하는 예외를 처리
        * 2. LoginProcessingFilter : 사용자의 로그인 요청을 처리 (SMS 인증을 통해, Access, Refresh Token 발급)
        * 3. RefreshTokenProcessingFilter : RefreshToken 재발급 요청 수행
        * 4. JwtAuthenticationFiler : AccessToken 의 유효성을 검사 후, SecurityContext 에 인증 객체 할당
         */
        http
                .addFilterAfter(authenticationExceptionFilter, LogoutFilter.class)
                .addFilterAfter(loginProcessingFilter, AuthenticationExceptionFilter.class)
                .addFilterAfter(refreshTokenProcessingFilter, LoginProcessingFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, ExceptionTranslationFilter.class);

        return http.build();
    }
}

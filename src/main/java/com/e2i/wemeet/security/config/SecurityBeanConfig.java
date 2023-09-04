package com.e2i.wemeet.security.config;

import com.e2i.wemeet.domain.cost.CostRepository;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.security.filter.JwtAuthenticationFilter;
import com.e2i.wemeet.security.filter.RefreshTokenProcessingFilter;
import com.e2i.wemeet.security.filter.RequestEndPointCheckFilter;
import com.e2i.wemeet.security.filter.SmsLoginProcessingFilter;
import com.e2i.wemeet.security.handler.CustomAuthenticationSuccessHandler;
import com.e2i.wemeet.security.handler.DispatcherServletEndPointChecker;
import com.e2i.wemeet.security.handler.HttpRequestEndPointChecker;
import com.e2i.wemeet.security.manager.CostAuthorizationManager;
import com.e2i.wemeet.security.provider.SmsCredentialAuthenticationProvider;
import com.e2i.wemeet.security.provider.SmsUserDetailsService;
import com.e2i.wemeet.security.token.TokenInjector;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.security.token.handler.RefreshTokenHandler;
import com.e2i.wemeet.service.credential.sms.SmsCredentialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class SecurityBeanConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> {
            web.ignoring().requestMatchers(PathRequest.toH2Console());
            web.ignoring().requestMatchers("/test", "/health");
            web.ignoring().requestMatchers("/css/**", "/js/**", "/favicon.ico");
        });
    }


    // 인증 과정에서 발생하는 예외를 처리하는 필터
    @Bean
    public AuthenticationExceptionFilter authenticationExceptionFilter(ObjectMapper objectMapper,
        MessageSourceAccessor accessor) {
        return new AuthenticationExceptionFilter(objectMapper, accessor);
    }

    // 로그인 인증 처리 필터
    @Bean
    public SmsLoginProcessingFilter loginAuthenticationFilter(AuthenticationManager manager,
        AuthenticationSuccessHandler successHandler, ObjectMapper objectMapper) {
        return new SmsLoginProcessingFilter(manager, successHandler, objectMapper);
    }

    // RefreshToken 재발급 요청 수행 필터
    @Bean
    public RefreshTokenProcessingFilter refreshTokenProcessingFilter(
        RedisTemplate<String, String> redisTemplate, RefreshTokenHandler refreshTokenHandler,
        TokenInjector tokenInjector, ObjectMapper objectMapper,
        AccessTokenHandler accessTokenHandler) {
        return new RefreshTokenProcessingFilter(redisTemplate, refreshTokenHandler, tokenInjector,
            objectMapper, accessTokenHandler);
    }

    @Bean
    public RequestEndPointCheckFilter requestEndPointCheckFilter(DispatcherServlet dispatcherServlet) {
        return new RequestEndPointCheckFilter(httpRequestEndPointChecker(dispatcherServlet));
    }

    // AccessToken 유효성 검증 필터
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AccessTokenHandler accessTokenHandler) {
        return new JwtAuthenticationFilter(accessTokenHandler);
    }

    // Authentication Manager -> Provider 에 로그인 요청을 전달
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration,
        AuthenticationProvider authenticationProvider) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        // Provider 등록
        List<AuthenticationProvider> provider = new ArrayList<>();
        provider.add(authenticationProvider);

        return new ProviderManager(provider, authenticationManager);
    }

    // Provider - 로그인 정보가 유효한지 확인 후 인증 객체 전달
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
        SmsCredentialService credentialService) {
        return new SmsCredentialAuthenticationProvider(userDetailsService, credentialService);
    }

    // UserDetailService - 유저가 입력한 값에 대한 인증 정보를 가져옴
    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return new SmsUserDetailsService(memberRepository);
    }

    // 사용자 로그인 요청 성공시 수행
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper,
        TokenInjector tokenInjector) {
        return new CustomAuthenticationSuccessHandler(objectMapper, tokenInjector);
    }

    /*
     * 권한 계층 적용
     * ADMIN > MANAGER > USER > GUEST
     * MANAGER > USER > GUEST
     * USER > GUEST
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
            """
                   ROLE_ADMIN > ROLE_MANAGER
                   ROLE_MANAGER > ROLE_USER
                   ROLE_USER > ROLE_GUEST
                """
        );
        return roleHierarchy;
    }

    // PreAuthorize 에 권한 계층 적용
    @Bean
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        methodSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return methodSecurityExpressionHandler;
    }

    // Credit 개수를 확인하는 AuthorizationManager
    @Bean
    public CostAuthorizationManager authorizationManager(MemberRepository memberRepository, CostRepository costRepository) {
        return new CostAuthorizationManager(memberRepository, costRepository, roleHierarchy());
    }

    // 요청을 처리할 수 있는 핸들러가 있는지 판별하는 컴포넌트
    @Bean
    public HttpRequestEndPointChecker httpRequestEndPointChecker(DispatcherServlet dispatcherServlet) {
        return new DispatcherServletEndPointChecker(dispatcherServlet);
    }
}

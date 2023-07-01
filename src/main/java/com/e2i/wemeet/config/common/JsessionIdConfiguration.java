package com.e2i.wemeet.config.common;

import static jakarta.servlet.SessionTrackingMode.COOKIE;

import jakarta.servlet.SessionCookieConfig;
import java.util.Collections;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* 사용하지 않는 JSESSIONID Cookie 비활성화
* */
@Configuration
public class JsessionIdConfiguration {

    @Bean
    public ServletContextInitializer clearJsession() {
        return servletContext -> {
            servletContext.setSessionTrackingModes(Collections.singleton(COOKIE));
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
        };
    }
}

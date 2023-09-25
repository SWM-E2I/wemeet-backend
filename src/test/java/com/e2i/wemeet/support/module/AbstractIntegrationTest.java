package com.e2i.wemeet.support.module;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.e2i.wemeet.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.security.filter.JwtAuthenticationFilter;
import com.e2i.wemeet.security.filter.RefreshTokenProcessingFilter;
import com.e2i.wemeet.security.filter.RequestEndPointCheckFilter;
import com.e2i.wemeet.security.filter.SmsLoginProcessingFilter;
import com.e2i.wemeet.security.handler.DispatcherServletEndPointChecker;
import com.e2i.wemeet.security.handler.HttpRequestEndPointChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/*
 * Integration Test Module
 * */
@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private SmsLoginProcessingFilter SMSLoginProcessingFilter;

    @Autowired
    private AuthenticationExceptionFilter authenticationExceptionFilter;

    @Autowired
    private RefreshTokenProcessingFilter refreshTokenProcessingFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    protected RequestEndPointCheckFilter requestEndPointCheckFilter;

    protected HttpRequestEndPointChecker httpRequestEndPointChecker;

    protected MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        DispatcherServlet dispatcherServlet = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation))
            .alwaysDo(MockMvcResultHandlers.print())
            .build().getDispatcherServlet();

        httpRequestEndPointChecker = new DispatcherServletEndPointChecker(dispatcherServlet);
        requestEndPointCheckFilter = new RequestEndPointCheckFilter(httpRequestEndPointChecker);

        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation))
            .addFilter(authenticationExceptionFilter)
            .addFilters(refreshTokenProcessingFilter, SMSLoginProcessingFilter, jwtAuthenticationFilter)
            .addFilter(requestEndPointCheckFilter)
            .build();
    }

    protected String toJson(Object data) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(data);
    }
}

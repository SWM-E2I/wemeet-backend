package com.e2i.wemeet.support.config;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.e2i.wemeet.config.security.filter.AuthenticationExceptionFilter;
import com.e2i.wemeet.config.security.filter.JwtAuthenticationFilter;
import com.e2i.wemeet.config.security.filter.SMSLoginProcessingFilter;
import com.e2i.wemeet.config.security.filter.RefreshTokenProcessingFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/*
* Integration Test Module
* */
@Transactional
@SpringBootTest
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private SMSLoginProcessingFilter SMSLoginProcessingFilter;

    @Autowired
    private AuthenticationExceptionFilter authenticationExceptionFilter;

    @Autowired
    private RefreshTokenProcessingFilter refreshTokenProcessingFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    protected MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation))
            .addFilters(authenticationExceptionFilter, SMSLoginProcessingFilter, refreshTokenProcessingFilter, jwtAuthenticationFilter)
            .build();
    }

    protected String toJson(Object data) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(data);
    }
}

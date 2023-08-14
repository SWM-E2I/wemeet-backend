package com.e2i.wemeet.support.config;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.e2i.wemeet.controller.TokenController;
import com.e2i.wemeet.controller.member.MemberController;
import com.e2i.wemeet.controller.team.TeamController;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.member_image.MemberImageService;
import com.e2i.wemeet.service.team.TeamService;
import com.e2i.wemeet.service.token.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@Import({TestWebBeanConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest({
    TokenController.class,
    TeamController.class,
    MemberController.class
})
public abstract class AbstractControllerUnitTest {

    @Autowired
    protected ObjectMapper mapper;

    @MockBean
    protected TeamService teamService;

    @MockBean
    protected CodeService codeService;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected MemberImageService memberImageService;

    @MockBean
    protected TokenService tokenService;


    protected MockMvc mockMvc;

    @BeforeEach
    void setUp(
        final WebApplicationContext context,
        final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation))
            .alwaysDo(MockMvcResultHandlers.print())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    protected String toJson(Object data) throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(data);
    }
}

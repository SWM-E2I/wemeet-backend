package com.e2i.wemeet.support.config;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.e2i.wemeet.controller.TokenController;
import com.e2i.wemeet.controller.credit.CreditController;
import com.e2i.wemeet.controller.heart.HeartController;
import com.e2i.wemeet.controller.meeting.MeetingController;
import com.e2i.wemeet.controller.member.BlockController;
import com.e2i.wemeet.controller.member.MemberController;
import com.e2i.wemeet.controller.member.RecommendController;
import com.e2i.wemeet.controller.suggestion.SuggestionController;
import com.e2i.wemeet.controller.team.TeamController;
import com.e2i.wemeet.controller.team.TeamImageController;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.credit.CreditService;
import com.e2i.wemeet.service.heart.HeartService;
import com.e2i.wemeet.service.meeting.MeetingHandleService;
import com.e2i.wemeet.service.meeting.MeetingListService;
import com.e2i.wemeet.service.member.BlockService;
import com.e2i.wemeet.service.member.MemberService;
import com.e2i.wemeet.service.member.RecommendService;
import com.e2i.wemeet.service.member_image.MemberImageService;
import com.e2i.wemeet.service.suggestion.SuggestionService;
import com.e2i.wemeet.service.team.TeamImageService;
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
    MemberController.class,
    MeetingController.class,
    SuggestionController.class,
    HeartController.class,
    CreditController.class,
    RecommendController.class,
    TeamImageController.class,
    BlockController.class
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
    @MockBean
    protected MeetingHandleService meetingHandleService;

    @MockBean
    protected MeetingListService meetingListService;

    @MockBean
    protected SuggestionService suggestionService;

    @MockBean
    protected HeartService heartService;

    @MockBean
    protected CreditService creditService;

    @MockBean
    protected RecommendService recommendService;

    @MockBean
    protected TeamImageService teamImageService;

    @MockBean
    protected BlockService blockService;

    @MockBean
    protected AccessTokenHandler accessTokenHandler;

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

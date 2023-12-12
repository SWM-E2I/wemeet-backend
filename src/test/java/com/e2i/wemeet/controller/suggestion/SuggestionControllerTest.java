package com.e2i.wemeet.controller.suggestion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionHistoryTeamDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.TeamLeaderResponseDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class SuggestionControllerTest extends AbstractControllerUnitTest {

    @DisplayName("오늘의 상대 팀 추천을 받을 수 있다.")
    @WithCustomMockUser
    @Test
    void readSuggestion_Success() throws Exception {
        SuggestionResponseDto response = SuggestionResponseDto.builder()
            .teamId(1L)
            .memberNum(2)
            .region("홍대")
            .profileImageURL("testImageUrl.jpg")
            .mainImageURL("testImageUrl.jpg")
            .leader(
                TeamLeaderResponseDto.builder()
                    .college("서울대학교")
                    .mbti(Mbti.ENFP.name())
                    .nickname("짱구")
                    .admissionYear("19")
                    .emailAuthenticated(true)
                    .build())
            .build();

        when(suggestionService.readSuggestion(anyLong(), any())).thenReturn(List.of(response));

        ResultActions perform = mockMvc.perform(get("/v1/suggestion"));
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Get Suggestion Success"),
                jsonPath("$.data[0].memberNum").value(response.memberNum()),
                jsonPath("$.data[0].region").value(response.region()),
                jsonPath("$.data[0].profileImageURL").value(response.profileImageURL()),
                jsonPath("$.data[0].leader.college").value(response.leader().college()),
                jsonPath("$.data[0].leader.mbti").value(Mbti.ENFP.name()),
                jsonPath("$.data[0].leader.nickname").value(response.leader().nickname()),
                jsonPath("$.data[0].leader.admissionYear").value(response.leader().admissionYear())
            );
        verify(suggestionService).readSuggestion(anyLong(), any());

        readSuggestionRestDocs(perform);
    }

    @DisplayName("오늘의 상대 팀 추천을 받았는지에 대한 여부를 확인할 수 있다.")
    @WithCustomMockUser
    @Test
    void checkTodaySuggestionHistory_Success() throws Exception {
        CheckSuggestionResponseDto response = CheckSuggestionResponseDto.builder()
            .isReceivedSuggestion(true)
            .teams(
                List.of(
                    SuggestionHistoryTeamDto.builder()
                        .teamId(1L)
                        .memberNum(2)
                        .region("홍대")
                        .profileImageURL("testImageUrl.jpg")
                        .mainImageURL("testImageUrl.jpg")
                        .leader(
                            TeamLeaderResponseDto.builder()
                                .college("서울대학교")
                                .mbti(Mbti.ENFP.name())
                                .nickname("짱구")
                                .admissionYear("19")
                                .emailAuthenticated(true)
                                .build())
                        .build()
                )
            )
            .build();

        when(suggestionService.checkTodaySuggestionHistory(anyLong(), any())).thenReturn(response);

        ResultActions perform = mockMvc.perform(get("/v1/suggestion/check"));
        perform
            .andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("SUCCESS"),
                jsonPath("$.message").value("Check Suggestion Success"),
                jsonPath("$.data.isReceivedSuggestion").value(response.isReceivedSuggestion())
            );

        verify(suggestionService).checkTodaySuggestionHistory(anyLong(), any());

        checkTodaySuggestionHistoryRestDocs(perform);
    }

    private void readSuggestionRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("오늘의 추천 받기",
                    ResourceSnippetParameters.builder()
                        .tag("추천 관련 API")
                        .summary("오늘의 추천 받기 API 입니다.")
                        .description(
                            """
                                    오늘의 추천 정보를 반환합니다.
                                    하루 3개의 추천 팀 정보를 받아올 수 있습니다.
                                    (오후 11:11 갱신)
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data[].teamId").type(JsonFieldType.NUMBER)
                            .description("추천 팀 아이디"),
                        fieldWithPath("data[].memberNum").type(JsonFieldType.NUMBER)
                            .description("추천 팀 인원수"),
                        fieldWithPath("data[].region").type(JsonFieldType.STRING)
                            .description("추천 팀 선호 지역"),
                        fieldWithPath("data[].profileImageURL").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 프로필 이미지"),
                        fieldWithPath("data[].mainImageURL").type(JsonFieldType.STRING)
                            .description("추천 팀 대표 이미지"),
                        fieldWithPath("data[].leader.nickname").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 닉네임"),
                        fieldWithPath("data[].leader.mbti").type(
                                JsonFieldType.STRING)
                            .description("추천 팀 팀장 MBTI"),
                        fieldWithPath("data[].leader.college").type(
                                JsonFieldType.STRING)
                            .description("추천 팀 팀장 대학교"),
                        fieldWithPath("data[].leader.emailAuthenticated").type(
                                JsonFieldType.BOOLEAN)
                            .description("추천 팀 팀장 대학 인증 여부"),
                        fieldWithPath("data[].leader.admissionYear").type(
                                JsonFieldType.STRING)
                            .description("추천 팀 팀장 학번")
                    )
                ));

    }

    private void checkTodaySuggestionHistoryRestDocs(ResultActions perform) throws Exception {
        perform
            .andDo(
                MockMvcRestDocumentationWrapper.document("오늘의 추천 여부 확인",
                    ResourceSnippetParameters.builder()
                        .tag("추천 관련 API")
                        .summary("오늘의 추천 받기 여부 확인 API 입니다.")
                        .description(
                            """
                                    오늘의 추천을 받았는지에 대한 정보를 확인할 수 있습니다.
                                    추천을 받았다면, isReceivedSuggestion은 true이고 추천 팀 정보가 반환됩니다.
                                """),
                    responseFields(
                        fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.isReceivedSuggestion").type(JsonFieldType.BOOLEAN)
                            .description("오늘의 추천 여부"),
                        fieldWithPath("data.teams[].teamId").type(JsonFieldType.NUMBER)
                            .description("추천 팀 아이디"),
                        fieldWithPath("data.teams[].isLiked").type(JsonFieldType.BOOLEAN)
                            .description("좋아요 여부"),
                        fieldWithPath("data.teams[].memberNum").type(JsonFieldType.NUMBER)
                            .description("추천 팀 인원수"),
                        fieldWithPath("data.teams[].region").type(JsonFieldType.STRING)
                            .description("추천 팀 선호 지역"),
                        fieldWithPath("data.teams[].profileImageURL").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 프로필 이미지"),
                        fieldWithPath("data.teams[].mainImageURL").type(JsonFieldType.STRING)
                            .description("추천 팀 대표 이미지"),
                        fieldWithPath("data.teams[].leader.nickname").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 닉네임"),
                        fieldWithPath("data.teams[].leader.mbti").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 MBTI"),
                        fieldWithPath("data.teams[].leader.college").type(JsonFieldType.STRING)
                            .description("추천 팀 팀장 대학교"),
                        fieldWithPath("data.teams[].leader.emailAuthenticated").type(
                                JsonFieldType.BOOLEAN)
                            .description("추천 팀 팀장 대학 인증 여부"),
                        fieldWithPath("data.teams[].leader.admissionYear").type(
                                JsonFieldType.STRING)
                            .description("추천 팀 팀장 학번")
                    )
                ));
    }
}

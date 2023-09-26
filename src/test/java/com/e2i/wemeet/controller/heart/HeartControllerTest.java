package com.e2i.wemeet.controller.heart;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.dto.response.heart.ReceivedHeartResponseDto;
import com.e2i.wemeet.dto.response.heart.SentHeartResponseDto;
import com.e2i.wemeet.dto.response.suggestion.TeamLeaderResponseDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class HeartControllerTest extends AbstractControllerUnitTest {

    @Nested
    class SendHeart {

        @DisplayName("좋아요를 보낼 수 있다.")
        @WithCustomMockUser(role = "MANAGER")
        @Test
        void sendHeart() throws Exception {
            // given
            final Long memberId = 1L;
            final Long partnerTeamId = 2L;
            final LocalDateTime requestTime = LocalDateTime.now();
            willDoNothing()
                .given(heartService).sendHeart(memberId, partnerTeamId, requestTime);

            // when
            ResultActions perform = mockMvc.perform(
                post("/v1/heart/{partnerTeamId}", partnerTeamId));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Send Heart Success"),
                    jsonPath("$.data").doesNotExist()
                );
            verify(heartService).sendHeart(anyLong(), anyLong(), any());

            sendHeartWriteRestDocs(perform);
        }

        private void sendHeartWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("좋아요 보내기",
                        ResourceSnippetParameters.builder()
                            .tag("좋아요 관련 API")
                            .summary("상대 팀에게 좋아요를 보냅니다.")
                            .description(
                                """
                                        상대 팀에게 좋아요를 보냅니다.
                                        하루에 한 번 가능하며, 본인 팀에게는 보낼 수 없습니다.

                                    """)
                            .pathParameters(
                                parameterWithName("partnerTeamId").description("상대 팀 아이디")
                            ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.NULL)
                                .description("data에는 아무 값도 반환되지 않습니다"))));
        }
    }

    @Nested
    class GetHeart {

        @DisplayName("보낸 좋아요 내역을 조회할 수 있다.")
        @WithCustomMockUser
        @Test
        void getSentHeart() throws Exception {
            // given
            final LocalDateTime requestTime = LocalDateTime.now();

            SentHeartResponseDto result = SentHeartResponseDto.builder()
                .teamId(1L)
                .region("신촌")
                .memberNum(3)
                .mainImageURL("https://test.image.com")
                .profileImageURL("https://test.image.com")
                .sentTime(requestTime)
                .leader(TeamLeaderResponseDto
                    .builder()
                    .nickname("팀장님")
                    .college("서울대")
                    .mbti("ENFP")
                    .admissionYear("19")
                    .build())
                .build();

            given(heartService.getSentHeart(anyLong(), any())).willReturn(List.of(result));

            // when
            ResultActions perform = mockMvc.perform(get("/v1/heart/sent"));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Get Sent Heart Detail Success"),
                    jsonPath("$.data[0].teamId").value(1L),
                    jsonPath("$.data[0].region").value("신촌"),
                    jsonPath("$.data[0].memberNum").value(3),
                    jsonPath("$.data[0].mainImageURL").value("https://test.image.com"),
                    jsonPath("$.data[0].profileImageURL").value("https://test.image.com"),
                    jsonPath("$.data[0].leader.nickname").value("팀장님"),
                    jsonPath("$.data[0].leader.college").value("서울대"),
                    jsonPath("$.data[0].leader.mbti").value("ENFP"),
                    jsonPath("$.data[0].leader.admissionYear").value("19")
                );
            verify(heartService).getSentHeart(anyLong(), any());

            getSentHeartWriteRestDocs(perform);
        }

        private void getSentHeartWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("보낸 좋아요 조회",
                        ResourceSnippetParameters.builder()
                            .tag("좋아요 관련 API")
                            .summary("보낸 좋아요 내역을 조회합니다.")
                            .description(
                                """
                                       내가 보낸 좋아요 내역을 조회합니다.
                                        팀이 없는 경우 조회되지 않습니다.
                                    """),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data[].teamId").type(JsonFieldType.NUMBER)
                                .description("상대 팀 아이디"),
                            fieldWithPath("data[].region").type(JsonFieldType.STRING)
                                .description("상대 팀 선호 지역"),
                            fieldWithPath("data[].memberNum").type(JsonFieldType.NUMBER)
                                .description("상대 팀 인원 수"),
                            fieldWithPath("data[].mainImageURL").type(JsonFieldType.STRING)
                                .description("상대 팀 메인 이미지 URL"),
                            fieldWithPath("data[].profileImageURL").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 프로필 이미지 URL"),
                            fieldWithPath("data[].sentTime").type(JsonFieldType.STRING)
                                .description("좋아요 보낸 시간"),
                            fieldWithPath("data[].leader.nickname").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 닉네임"),
                            fieldWithPath("data[].leader.college").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 대학교"),
                            fieldWithPath("data[].leader.mbti").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 MBTI"),
                            fieldWithPath("data[].leader.admissionYear").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 학번")
                        )));
        }

        @DisplayName("받은 좋아요 내역을 조회할 수 있다.")
        @WithCustomMockUser
        @Test
        void getReceivedHeart() throws Exception {
            // given
            final LocalDateTime requestTime = LocalDateTime.now();

            ReceivedHeartResponseDto result = ReceivedHeartResponseDto.builder()
                .teamId(1L)
                .region("신촌")
                .memberNum(3)
                .mainImageURL("https://test.image.com")
                .profileImageURL("https://test.image.com")
                .receivedTime(requestTime)
                .leader(TeamLeaderResponseDto
                    .builder()
                    .nickname("팀장님")
                    .college("서울대")
                    .mbti("ENFP")
                    .admissionYear("19")
                    .build())
                .build();

            given(heartService.getReceivedHeart(anyLong(), any())).willReturn(List.of(result));

            // when
            ResultActions perform = mockMvc.perform(get("/v1/heart/received"));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Get Received Heart Detail Success"),
                    jsonPath("$.data[0].teamId").value(1L),
                    jsonPath("$.data[0].region").value("신촌"),
                    jsonPath("$.data[0].memberNum").value(3),
                    jsonPath("$.data[0].mainImageURL").value("https://test.image.com"),
                    jsonPath("$.data[0].profileImageURL").value("https://test.image.com"),
                    jsonPath("$.data[0].leader.nickname").value("팀장님"),
                    jsonPath("$.data[0].leader.college").value("서울대"),
                    jsonPath("$.data[0].leader.mbti").value("ENFP"),
                    jsonPath("$.data[0].leader.admissionYear").value("19")
                );
            verify(heartService).getReceivedHeart(anyLong(), any());

            getReceivedHeartWriteRestDocs(perform);
        }

        private void getReceivedHeartWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("받은 좋아요 조회",
                        ResourceSnippetParameters.builder()
                            .tag("좋아요 관련 API")
                            .summary("받은 좋아요 내역을 조회합니다.")
                            .description(
                                """
                                       나의 팀이 받은 좋아요 내역을 조회합니다.
                                        팀이 없는 경우 조회되지 않습니다.
                                    """),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data[].teamId").type(JsonFieldType.NUMBER)
                                .description("상대 팀 아이디"),
                            fieldWithPath("data[].region").type(JsonFieldType.STRING)
                                .description("상대 팀 선호 지역"),
                            fieldWithPath("data[].memberNum").type(JsonFieldType.NUMBER)
                                .description("상대 팀 인원 수"),
                            fieldWithPath("data[].mainImageURL").type(JsonFieldType.STRING)
                                .description("상대 팀 메인 이미지 URL"),
                            fieldWithPath("data[].profileImageURL").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 프로필 이미지 URL"),
                            fieldWithPath("data[].receivedTime").type(JsonFieldType.STRING)
                                .description("좋아요 받은 시간"),
                            fieldWithPath("data[].leader.nickname").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 닉네임"),
                            fieldWithPath("data[].leader.college").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 대학교"),
                            fieldWithPath("data[].leader.mbti").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 MBTI"),
                            fieldWithPath("data[].leader.admissionYear").type(JsonFieldType.STRING)
                                .description("상대 팀 팀장 학번")
                        )));
        }
    }
}
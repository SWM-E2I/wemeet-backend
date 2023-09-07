package com.e2i.wemeet.controller.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.dto.dsl.MeetingInformationDto;
import com.e2i.wemeet.dto.dsl.MeetingRequestInformationDto;
import com.e2i.wemeet.dto.request.meeting.MeetingRequestAcceptDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.support.config.AbstractControllerUnitTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class MeetingControllerTest extends AbstractControllerUnitTest {

    @Nested
    class Request {

        @DisplayName("미팅을 신청할 수 있다.")
        @WithCustomMockUser
        @Test
        void sendMeetingRequest() throws Exception {
            // given
            final Long memberId = 1L;
            final SendMeetingRequestDto request = new SendMeetingRequestDto(1L);
            willDoNothing()
                .given(meetingHandleService).sendRequest(request, memberId);

            // when
            ResultActions perform = mockMvc.perform(post("/v1/meeting")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Send meeting request success"),
                    jsonPath("$.data").doesNotExist()
                );
            verify(meetingHandleService).sendRequest(request, memberId);

            sendMeetingRequestWriteRestDocs(perform);
        }

        @DisplayName("쪽지와 함께 미팅을 신청할 수 있다.")
        @WithCustomMockUser
        @Test
        void sendMeetingRequestWithMessage() throws Exception {
            // given
            final Long memberId = 1L;
            final String message = "a".repeat(50);
            final SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(
                1L, message);
            willDoNothing()
                .given(meetingHandleService).sendRequestWithMessage(request, memberId);

            // when
            ResultActions perform = mockMvc.perform(post("/v1/meeting/message")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Send meeting request with message success"),
                    jsonPath("$.data").doesNotExist()
                );
            verify(meetingHandleService).sendRequestWithMessage(request, memberId);

            sendMeetingRequestWithMessageWriteRestDocs(perform);
        }

        @DisplayName("쪽지가 정해진 형식에 어긋난다면 미팅 신청에 실패한다.")
        @MethodSource("provideMessagesForMeetingRequest")
        @ParameterizedTest
        void sendMeetingRequestWithMessageWithLongMessages(String message) throws Exception {
            // given
            final Long memberId = 1L;
            final SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(
                1L, message);
            willDoNothing()
                .given(meetingHandleService).sendRequestWithMessage(request, memberId);

            // when
            ResultActions perform = mockMvc.perform(post("/v1/meeting/message")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));

            // then
            perform
                .andExpectAll(
                    status().isInternalServerError(),
                    jsonPath("$.status").value("ERROR"),
                    jsonPath("$.message").value("서버에서 예상치 못한 예외가 발생했습니다"),
                    jsonPath("$.data").doesNotExist()
                );
            verify(meetingHandleService, times(0)).sendRequestWithMessage(request, memberId);
        }

        public static Stream<String> provideMessagesForMeetingRequest() {
            return Stream.of(
                "a".repeat(51),
                "아".repeat(51),
                "",
                "  "
            );
        }

        private void sendMeetingRequestWithMessageWriteRestDocs(ResultActions perform)
            throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("쪽지와 미팅 신청",
                        ResourceSnippetParameters.builder()
                            .tag("쪽지와 함께 미팅 신청")
                            .summary("상대 이성 팀에게 쪽지와 함께 미팅을 신청합니다.")
                            .description(
                                """
                                        상대 이성 팀에게 쪽지와 함께 미팅을 신청합니다.
                                        지정된 개수 만큼의 크레딧이 소모됩니다. (12)
                                    """),
                        requestFields(
                            fieldWithPath("partnerTeamId").type(JsonFieldType.NUMBER)
                                .description("상대 이성 팀 ID"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("쪽지 내용 (최대 50자)")
                        ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.NULL)
                                .description("data에는 아무 값도 반환되지 않습니다")
                        )
                    ));
        }

        private void sendMeetingRequestWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("미팅 신청",
                        ResourceSnippetParameters.builder()
                            .tag("미팅 신청")
                            .summary("상대 이성 팀에게 미팅을 신청합니다.")
                            .description(
                                """
                                        상대 이성 팀에게 미팅을 신청합니다.
                                        지정된 개수 만큼의 크레딧이 소모됩니다. (10)
                                    """),
                        requestFields(
                            fieldWithPath("partnerTeamId").type(JsonFieldType.NUMBER)
                                .description("상대 이성 팀 ID")
                        ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.NULL)
                                .description("data에는 아무 값도 반환되지 않습니다")
                        )
                    ));
        }

    }

    @Nested
    class HandleRequest {

        @DisplayName("미팅 신청을 수락할 수 있다.")
        @WithCustomMockUser
        @Test
        void acceptMeetingRequest() throws Exception {
            // given
            final Long memberId = 1L;
            final Long meetingRequestId = 1L;
            final MeetingRequestAcceptDto request = new MeetingRequestAcceptDto(
                "https://open.kakao.com/o/S13kdfs1");
            given(meetingHandleService.acceptRequest(anyLong(), anyLong(), any(LocalDateTime.class)))
                .willReturn(1L);

            // when
            ResultActions perform = mockMvc.perform(post("/v1/meeting/accept/" + meetingRequestId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Meeting was successfully matched"),
                    jsonPath("$.data").value(1L)
                );
            verify(meetingHandleService).acceptRequest(anyLong(), anyLong(), any(LocalDateTime.class));

            acceptMeetingRequestWriteRestDocs(perform);
        }

        @DisplayName("미팅 신청을 거절할 수 있다.")
        @WithCustomMockUser
        @Test
        void rejectMeetingRequest() throws Exception {
            // given
            final Long meetingRequestId = 1L;
            given(
                meetingHandleService.rejectRequest(anyLong(), anyLong(), any(LocalDateTime.class)))
                .willReturn(AcceptStatus.REJECT);

            // when
            ResultActions perform = mockMvc.perform(post("/v1/meeting/reject/" + meetingRequestId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Meeting request successfully Rejected"),
                    jsonPath("$.data").value(AcceptStatus.REJECT.name())
                );
            verify(meetingHandleService).rejectRequest(anyLong(), anyLong(),
                any(LocalDateTime.class));

            rejectMeetingRequestWriteRestDocs(perform);
        }

        private void acceptMeetingRequestWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("미팅 신청 수락",
                        ResourceSnippetParameters.builder()
                            .tag("미팅 신청 수락")
                            .summary("미팅 신청을 수락합니다.")
                            .description(
                                """
                                        상대 이성 팀으로부터 받은 미팅 신청을 수락합니다.
                                        지정된 개수 만큼의 크레딧이 소모됩니다. (5)
                                    """),
                        requestFields(
                            fieldWithPath("kakaoOpenChatLink").type(JsonFieldType.STRING)
                                .description("미팅 성사 후 대화를 나눌 카카오톡 채팅방")
                        ),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.NUMBER)
                                .description("미팅 ID가 반환됩니다.")
                        )
                    ));
        }

        private void rejectMeetingRequestWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("미팅 신청 거절",
                        ResourceSnippetParameters.builder()
                            .tag("미팅 신청 거절")
                            .summary("미팅 신청을 거절합니다.")
                            .description(
                                """
                                        상대 이성 팀으로부터 받은 미팅 신청을 거절합니다.
                                    """),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data").type(JsonFieldType.STRING)
                                .description("거절 상태가 반환됩니다.")
                        )
                    ));
        }

    }

    @Nested
    class GetRequestList {

        @DisplayName("미팅 리스트를 조회할 수 있다.")
        @WithCustomMockUser
        @Test
        void getAcceptedMeetingList() throws Exception {
            // given
            final MeetingInformationDto meetingInformationDto1 = new MeetingInformationDto(1L,
                LocalDateTime.now(), false, null, 1L, 2,
                Region.HONGDAE, 1L,
                "채원", Mbti.INFP,
                "https://profile.com", "고려대학교",
                CollegeType.ENGINEERING, "2022", true);
            final MeetingInformationDto meetingInformationDto2 = new MeetingInformationDto(2L,
                LocalDateTime.now(), false, null, 2L, 2,
                Region.HONGDAE, 2L,
                "째림", Mbti.INFJ,
                "https://profile.com", "서울대학교",
                CollegeType.ARTS, "2019", true);

            List<AcceptedMeetingResponseDto> responseDtos = List.of(
                AcceptedMeetingResponseDto.of(meetingInformationDto1,
                    List.of("https://profile1.com")),
                AcceptedMeetingResponseDto.of(meetingInformationDto2,
                    List.of("https://profile2.com"))
            );
            given(meetingListService.getAcceptedMeetingList(anyLong(), any(LocalDateTime.class)))
                .willReturn(responseDtos);

            // when
            ResultActions perform = mockMvc.perform(get("/v1/meeting/accepted")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Get accepted meeting list success"),
                    jsonPath("$.data[0].meetingId").value(1L),
                    jsonPath("$.data[0].teamId").value(1L),
                    jsonPath("$.data[0].memberCount").value(2),
                    jsonPath("$.data[0].region").value(Region.HONGDAE.name()),
                    jsonPath("$.data[0].deleted").value(false),
                    jsonPath("$.data[0].meetingAcceptTime").isString(),
                    jsonPath("$.data[0].teamProfileImageUrl").isArray(),
                    jsonPath("$.data[0].leader.leaderId").value(1L),
                    jsonPath("$.data[0].leader.nickname").value("채원"),
                    jsonPath("$.data[0].leader.mbti").value(Mbti.INFP.name()),
                    jsonPath("$.data[0].leader.collegeName").value("고려대학교"),
                    jsonPath("$.data[0].leader.collegeType").value(CollegeType.ENGINEERING.name()),
                    jsonPath("$.data[0].leader.admissionYear").value("2022"),
                    jsonPath("$.data[0].leader.imageAuth").value(true),
                    jsonPath("$.data[0].leader.leaderLowProfileImageUrl").value(
                        "https://profile.com"),
                    jsonPath("$.data[0].expired").value(false)
                );
            verify(meetingListService).getAcceptedMeetingList(anyLong(), any(LocalDateTime.class));

            getAcceptMeetingListWriteRestDocs(perform);
        }

        @DisplayName("보낸 미팅 신청 리스트를 조회할 수 있다.")
        @WithCustomMockUser
        @Test
        void getSentMeetingRequestList() throws Exception {
            // given
            final MeetingRequestInformationDto requestInformationDto1 = new MeetingRequestInformationDto(
                1L, LocalDateTime.now(), "재미있게 놀아요!",
                PENDING, 1L, 4,
                Region.HONGDAE, null, 1L,
                "채원", Mbti.INFP,
                "https://profile.com", "고려대학교",
                CollegeType.ENGINEERING, "2022", true);
            final MeetingRequestInformationDto requestInformationDto2 = new MeetingRequestInformationDto(
                2L, LocalDateTime.now(), "재미있게 놀아요!",
                PENDING, 2L, 4,
                Region.HONGDAE, null, 2L,
                "쨰림", Mbti.INFJ,
                "https://profile.com", "서울대학교",
                CollegeType.ARTS, "2019", true);

            List<SentMeetingResponseDto> sentMeetingResponseDtos = List.of(
                SentMeetingResponseDto.of(requestInformationDto1, List.of("https://profile1.com")),
                SentMeetingResponseDto.of(requestInformationDto2, List.of("https://profile2.com"))
            );
            given(meetingListService.getSentRequestList(anyLong(), any(LocalDateTime.class)))
                .willReturn(sentMeetingResponseDtos);

            // when
            ResultActions perform = mockMvc.perform(get("/v1/meeting/sent")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Get sent meeting request list success"),
                    jsonPath("$.data[0].meetingRequestId").value(1),
                    jsonPath("$.data[0].acceptStatus").value(PENDING.name()),
                    jsonPath("$.data[0].requestTime").isString(),
                    jsonPath("$.data[0].partnerTeamDeleted").value(false),
                    jsonPath("$.data[0].teamId").value(1),
                    jsonPath("$.data[0].memberCount").value(4),
                    jsonPath("$.data[0].region").value(Region.HONGDAE.name()),
                    jsonPath("$.data[0].message").isString(),
                    jsonPath("$.data[0].teamProfileImageUrl").isArray(),
                    jsonPath("$.data[0].leader.leaderId").value(1),
                    jsonPath("$.data[0].leader.nickname").value("채원"),
                    jsonPath("$.data[0].leader.mbti").value(Mbti.INFP.name()),
                    jsonPath("$.data[0].leader.collegeName").value("고려대학교"),
                    jsonPath("$.data[0].leader.leaderLowProfileImageUrl").value("https://profile.com"),
                    jsonPath("$.data[0].leader.collegeType").value(CollegeType.ENGINEERING.name()),
                    jsonPath("$.data[0].leader.admissionYear").value("2022"),
                    jsonPath("$.data[0].leader.imageAuth").value(true)
                );
            verify(meetingListService).getSentRequestList(anyLong(), any(LocalDateTime.class));

            getMeetingListWriteRestDocs(perform);
        }

        @DisplayName("받은 미팅 신청 리스트를 조회할 수 있다.")
        @WithCustomMockUser
        @Test
        void getReceiveMeetingRequestList() throws Exception {
            // given
            final MeetingRequestInformationDto requestInformationDto1 = new MeetingRequestInformationDto(
                1L, LocalDateTime.now(), "재미있게 놀아요!",
                PENDING,
                1L, 4,
                Region.HONGDAE, null, 1L,
                "채원", Mbti.INFP,
                "https://profile.com", "고려대학교",
                CollegeType.ENGINEERING, "2022", true);
            final MeetingRequestInformationDto requestInformationDto2 = new MeetingRequestInformationDto(
                2L, LocalDateTime.now(), "재미있게 놀아요!",
                PENDING,
                2L, 4,
                Region.HONGDAE, null, 2L,
                "쨰림", Mbti.INFJ,
                "https://profile.com", "서울대학교",
                CollegeType.ARTS, "2019", true);

            List<ReceivedMeetingResponseDto> receivedMeetingResponseDtos = List.of(
                ReceivedMeetingResponseDto.of(requestInformationDto1,
                    List.of("https://profile1.com")),
                ReceivedMeetingResponseDto.of(requestInformationDto2,
                    List.of("https://profile2.com"))
            );
            given(meetingListService.getReceiveRequestList(anyLong(), any(LocalDateTime.class)))
                .willReturn(receivedMeetingResponseDtos);

            // when
            ResultActions perform = mockMvc.perform(get("/v1/meeting/received")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON));

            // then
            perform
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status").value("SUCCESS"),
                    jsonPath("$.message").value("Get receive meeting request list success"),
                    jsonPath("$.data[0].meetingRequestId").value(1),
                    jsonPath("$.data[0].acceptStatus").value(PENDING.name()),
                    jsonPath("$.data[0].requestTime").isString(),
                    jsonPath("$.data[0].partnerTeamDeleted").value(false),
                    jsonPath("$.data[0].teamId").value(1),
                    jsonPath("$.data[0].memberCount").value(4),
                    jsonPath("$.data[0].region").value(Region.HONGDAE.name()),
                    jsonPath("$.data[0].message").isString(),
                    jsonPath("$.data[0].teamProfileImageUrl").isArray(),
                    jsonPath("$.data[0].leader.leaderId").value(1),
                    jsonPath("$.data[0].leader.nickname").value("채원"),
                    jsonPath("$.data[0].leader.mbti").value(Mbti.INFP.name()),
                    jsonPath("$.data[0].leader.collegeName").value("고려대학교"),
                    jsonPath("$.data[0].leader.leaderLowProfileImageUrl").value(
                        "https://profile.com")
                );
            verify(meetingListService).getReceiveRequestList(anyLong(), any(LocalDateTime.class));

            getMeetingListWriteRestDocs(perform);
        }

        private void getAcceptMeetingListWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("성사된 미팅 리스트 조회",
                        ResourceSnippetParameters.builder()
                            .tag("성사된 미팅 리스트 조회")
                            .summary("성사된 미팅 리스트를 조회합니다.")
                            .description(
                                """
                                        성사된 미팅 리스트를 조회합니다.
                                        상대 팀이 삭제되었거나, 미팅 유효기간이 만료된 경우에는 리스트에 포함되지 않습니다.
                                    """),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data[].meetingId").type(JsonFieldType.NUMBER)
                                .description("미팅 ID"),
                            fieldWithPath("data[].teamId").type(JsonFieldType.NUMBER)
                                .description("팀 ID"),
                            fieldWithPath("data[].memberCount").type(JsonFieldType.NUMBER)
                                .description("팀 인원 수"),
                            fieldWithPath("data[].region").type(JsonFieldType.STRING)
                                .description("지역"),
                            fieldWithPath("data[].deleted").type(JsonFieldType.BOOLEAN)
                                .description("미팅 삭제 여부"),
                            fieldWithPath("data[].meetingAcceptTime").type(JsonFieldType.STRING)
                                .description("미팅 수락 시간"),
                            fieldWithPath("data[].teamProfileImageUrl").type(JsonFieldType.ARRAY)
                                .description("팀 프로필 이미지 URL"),
                            fieldWithPath("data[].leader.leaderId").type(JsonFieldType.NUMBER)
                                .description("팀장 ID"),
                            fieldWithPath("data[].leader.nickname").type(JsonFieldType.STRING)
                                .description("팀장 닉네임"),
                            fieldWithPath("data[].leader.mbti").type(JsonFieldType.STRING)
                                .description("팀장 MBTI"),
                            fieldWithPath("data[].leader.collegeName").type(JsonFieldType.STRING)
                                .description("팀장 대학교 이름"),
                            fieldWithPath("data[].leader.collegeType").type(JsonFieldType.STRING)
                                .description("팀장 학과"),
                            fieldWithPath("data[].leader.admissionYear").type(JsonFieldType.STRING)
                                .description("팀장 학번"),
                            fieldWithPath("data[].leader.imageAuth").type(JsonFieldType.BOOLEAN)
                                .description("팀장 프로필 인증 여부"),
                            fieldWithPath("data[].leader.leaderLowProfileImageUrl").type(
                                JsonFieldType.STRING).description("팀장 저화질 프로필 이미지 URL"),
                            fieldWithPath("data[].expired").type(JsonFieldType.BOOLEAN)
                                .description("미팅 만료 여부")
                        )
                    ));
        }

        private void getMeetingListWriteRestDocs(ResultActions perform) throws Exception {
            perform
                .andDo(
                    MockMvcRestDocumentationWrapper.document("보낸 신청 리스트 조회",
                        ResourceSnippetParameters.builder()
                            .tag("보낸 신청 리스트 조회")
                            .summary("보낸 신청 리스트를 조회합니다.")
                            .description(
                                """
                                        보낸 신청 리스트를 조회합니다.
                                        상대 팀이 삭제되었거나, 미팅 유효기간이 만료된 경우에는 리스트에 포함되지 않습니다.
                                    """),
                        responseFields(
                            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                            fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메시지"),
                            fieldWithPath("data[].meetingRequestId").type(JsonFieldType.NUMBER)
                                .description("미팅 신청 ID"),
                            fieldWithPath("data[].acceptStatus").type(JsonFieldType.STRING)
                                .description("응답 상태"),
                            fieldWithPath("data[].requestTime").type(JsonFieldType.STRING)
                                .description("미팅 신청 시간"),
                            fieldWithPath("data[].partnerTeamDeleted").type(JsonFieldType.BOOLEAN)
                                .description("상대 팀 삭제 여부"),
                            fieldWithPath("data[].teamId").type(JsonFieldType.NUMBER)
                                .description("팀 ID"),
                            fieldWithPath("data[].memberCount").type(JsonFieldType.NUMBER)
                                .description("팀 인원 수"),
                            fieldWithPath("data[].region").type(JsonFieldType.STRING)
                                .description("지역"),
                            fieldWithPath("data[].message").type(JsonFieldType.STRING)
                                .description("쪽지 내용"),
                            fieldWithPath("data[].teamProfileImageUrl").type(JsonFieldType.ARRAY)
                                .description("팀 프로필 이미지 URL"),
                            fieldWithPath("data[].leader.leaderId").type(JsonFieldType.NUMBER)
                                .description("팀장 ID"),
                            fieldWithPath("data[].leader.nickname").type(JsonFieldType.STRING)
                                .description("팀장 닉네임"),
                            fieldWithPath("data[].leader.mbti").type(JsonFieldType.STRING)
                                .description("팀장 MBTI"),
                            fieldWithPath("data[].leader.collegeName").type(JsonFieldType.STRING)
                                .description("팀장 대학교 이름"),
                            fieldWithPath("data[].leader.leaderLowProfileImageUrl").type(
                                JsonFieldType.STRING).description("팀장 저화질 프로필 이미지 URL"),
                            fieldWithPath("data[].leader.collegeType").type(JsonFieldType.STRING)
                                .description("팀장 학과"),
                            fieldWithPath("data[].leader.admissionYear").type(JsonFieldType.STRING)
                                .description("팀장 학번"),
                            fieldWithPath("data[].leader.imageAuth").type(JsonFieldType.BOOLEAN)
                                .description("팀장 프로필 인증 여부"),
                            fieldWithPath("data[].pending").type(JsonFieldType.BOOLEAN)
                                .description("미팅 신청 대기 여부"),
                            fieldWithPath("data[].deleted").type(JsonFieldType.BOOLEAN)
                                .description("미팅 신청 삭제 여부")
                        )
                    ));
        }


    }

}
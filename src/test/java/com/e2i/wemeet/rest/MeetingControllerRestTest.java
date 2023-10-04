package com.e2i.wemeet.rest;

import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_POST_요청을_보낸다;
import static com.e2i.wemeet.security.token.JwtEnv.ACCESS;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.KARINA;
import static com.e2i.wemeet.support.fixture.RestAssuredFixture.카이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.rest.support.MultipartRequest;
import com.e2i.wemeet.support.module.AbstractAcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingControllerRestTest extends AbstractAcceptanceTest {

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @DisplayName("회원가입, 사진 등록 후 팀을 생성하고 미팅을 신청할 수 있다.")
    @Test
    void meetingRequest() throws IOException {
        // given
        MultipartRequest<Object> images = createMultiPartRequest(
            "images", "classpath:/static/test_image/software maestro.png");

        final Long womanTeamId = 여자_4인팀을_생성한다(KARINA.create(WOMANS_CODE)).team().getTeamId();
        String accessToken = 카이.회원가입하고_1번_팀을_생성한다(images).header(ACCESS.getKey());

        SendMeetingRequestDto requestDto = new SendMeetingRequestDto(womanTeamId);

        // when
        final String url = "/v1/meeting";
        ExtractableResponse<Response> response = 로그인된_상태로_POST_요청을_보낸다(url, accessToken, requestDto);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("미팅 신청 목록에서 ID를 확인한 뒤 수락할 수 있다.")
    @Test
    void requestAndAccept() {
        // given
        CreationData womanData = 여자_4인팀을_생성한다(KARINA.create(WOMANS_CODE));
        CreationData manData = 남자_4인팀을_생성한다(KAI.create(ANYANG_CODE));

        미팅을_신청한다(manData, womanData);
        SendMeetingRequestDto meetingRequest = new SendMeetingRequestDto(womanData.team().getTeamId());

        // when
        List<MeetingRequest> findRequest = meetingRequestRepository.findAllByMemberId(womanData.member().getMemberId());
        ExtractableResponse<Response> response = 로그인된_상태로_POST_요청을_보낸다(
            "/v1/meeting/accept/" + findRequest.get(0).getMeetingRequestId(), womanData.accessToken(), meetingRequest);

        // then
        List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingRepository.findAcceptedMeetingList(womanData.member().getMemberId());
        long meetingId = response.body().jsonPath().getLong("data");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(acceptedMeetingList).hasSize(1)
            .extracting("meetingId", "teamId")
            .contains(
                tuple(meetingId, manData.team().getTeamId())
            );
    }

    private MeetingRequest 미팅을_신청한다(CreationData manData, CreationData womanData) {
        return meetingRequestRepository.save(MeetingRequest.builder()
            .team(manData.team())
            .partnerTeam(womanData.team())
            .build());
    }

}
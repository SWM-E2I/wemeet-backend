package com.e2i.wemeet.rest;

import static com.e2i.wemeet.domain.member.data.Gender.MAN;
import static com.e2i.wemeet.domain.member.data.Gender.WOMAN;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_DELETE_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_GET_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_이미지와_함께_PUT_요청을_보낸다;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.RestAssuredFixture.째림;
import static com.e2i.wemeet.support.fixture.RestAssuredFixture.카이;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_2;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.createRequestDto_2_man;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.rest.support.MultipartRequest;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.support.module.AbstractAcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamControllerRestTest extends AbstractAcceptanceTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 가입을 하고 팀을 생성할 수 있다.")
    @Test
    void createTeam() throws IOException {
        // given
        final MultipartRequest<Object> imageRequest = createMultiPartRequest("images",
            "classpath:/static/test_image/software maestro.png");

        // when
        ExtractableResponse<Response> response = 카이.회원가입하고_1번_팀을_생성한다(imageRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("팀을 생성하고 수정할 수 있다")
    @Test
    void updateTeam() throws IOException {
        // given
        final MultipartRequest<Object> imageRequest = createMultiPartRequest("images",
            "classpath:/static/test_image/software maestro.png");
        UpdateTeamRequestDto updateTeamRequestDto = HONGDAE_TEAM_2.updateTeamRequestDto(createRequestDto_2_man());

        String accessToken = 카이.회원가입을_한다();

        // when
        카이.홍대1번_팀을_생성한다(accessToken, MAN, imageRequest);
        ExtractableResponse<Response> updateResponse = 로그인된_상태로_이미지와_함께_PUT_요청을_보낸다("/v1/team", accessToken,
            imageRequest, updateTeamRequestDto);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(200);
    }

    @DisplayName("팀을 생성하고 삭제한 뒤, 다시 팀을 생성할 수 있다.")
    @Test
    void createAndDeleteAndCreate() throws IOException {
        // given
        final MultipartRequest<Object> imageRequest = createMultiPartRequest("images",
            "classpath:/static/test_image/software maestro.png");

        String accessToken = 카이.회원가입을_한다();

        // when
        카이.홍대1번_팀을_생성한다(accessToken, MAN, imageRequest);
        로그인된_상태로_DELETE_요청을_보낸다("/v1/team", accessToken);
        ExtractableResponse<Response> response = 카이.홍대2번_팀을_생성한다(accessToken, MAN, imageRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("다른 팀을 조회할 수 있다.")
    @Test
    void readOtherTeam() throws IOException {
        // given
        final MultipartRequest<Object> imageRequest = createMultiPartRequest("images",
            "classpath:/static/test_image/software maestro.png");

        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        String kaiAccessToken = accessTokenHandler.createToken(new Payload(rim.getMemberId(), "USER"));
        String rimAccessToken = accessTokenHandler.createToken(new Payload(rim.getMemberId(), "USER"));

        째림.홍대1번_팀을_생성한다(rimAccessToken, WOMAN, imageRequest);
        Long rimTeamId = teamRepository.findByMemberId(rim.getMemberId()).get().getTeamId();

        // when
        ExtractableResponse<Response> response = 로그인된_상태로_GET_요청을_보낸다("/v1/team/" + rimTeamId, kaiAccessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().jsonPath().getString("data.leader.nickname"))
            .isEqualTo(째림.getFixture().getNickname());
    }

}
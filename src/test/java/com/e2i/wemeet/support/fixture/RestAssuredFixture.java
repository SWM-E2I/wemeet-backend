package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.POST_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_파일과_함께_POST_요청을_보낸다;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_2;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.createRequestDto_2_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.createRequestDto_2_woman;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.createRequestDto_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.createRequestDto_3_woman;

import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.rest.support.MultipartRequest;
import com.e2i.wemeet.security.token.JwtEnv;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RestAssuredFixture {
    카이(MemberFixture.KAI),
    째림(MemberFixture.RIM),
    유니(MemberFixture.SEYUN),
    워니(MemberFixture.CHAEWON),
    정열(MemberFixture.JEONGYEOL),
    카리나(MemberFixture.KARINA),
    ;

    private final MemberFixture memberFixture;

    RestAssuredFixture(MemberFixture memberFixture) {
        this.memberFixture = memberFixture;
    }

    // @return accessToken
    public String 회원가입을_한다() {
        final CreateMemberRequestDto request = memberFixture.createMemberRequestDto();
        final String registerUrl = "/v1/member";
        ExtractableResponse<Response> response = POST_요청을_보낸다(registerUrl, request);
        return response.header(JwtEnv.ACCESS.getKey());
    }

    public ExtractableResponse<Response> 회원가입하고_1번_팀을_생성한다(MultipartRequest<Object>... images) {
        String accessToken = 회원가입을_한다();
        사진을_등록한다(images, accessToken);
        return 홍대1번_팀을_생성한다(accessToken, this.memberFixture.getGender(), images);
    }

    public void 사진을_등록한다(MultipartRequest<Object>[] images, String accessToken) {
        로그인된_상태로_파일과_함께_POST_요청을_보낸다("/v1/member/profile-image", accessToken, MultipartRequest.of("file", images[0].data()));
    }

    public ExtractableResponse<Response> 회원가입하고_2번_팀을_생성한다(MultipartRequest<Object>... images) {
        String accessToken = 회원가입을_한다();
        사진을_등록한다(images, accessToken);
        return 홍대2번_팀을_생성한다(accessToken, this.memberFixture.getGender(), images);
    }

    public ExtractableResponse<Response> 홍대1번_팀을_생성한다(String accessToken, Gender gender, MultipartRequest<Object>... images) {
        CreateTeamRequestDto createTeamRequestDto;

        if (gender == Gender.WOMAN) {
            createTeamRequestDto = HONGDAE_TEAM_1.createTeamRequestDto(createRequestDto_3_woman());
        } else {
            createTeamRequestDto = HONGDAE_TEAM_1.createTeamRequestDto(createRequestDto_3_man());
        }

        List<MultipartRequest<Object>> objects = new ArrayList<>(Arrays.asList(images));
        final String url = "/v1/team";
        return 로그인된_상태로_파일과_함께_POST_요청을_보낸다(url, accessToken, objects, createTeamRequestDto);
    }

    public ExtractableResponse<Response> 홍대2번_팀을_생성한다(String accessToken, Gender gender, MultipartRequest<Object>... images) {
        CreateTeamRequestDto createTeamRequestDto;

        if (this.memberFixture.getGender() == Gender.WOMAN) {
            createTeamRequestDto = HONGDAE_TEAM_2.createTeamRequestDto(createRequestDto_2_woman());
        } else {
            createTeamRequestDto = HONGDAE_TEAM_2.createTeamRequestDto(createRequestDto_2_man());
        }

        List<MultipartRequest<Object>> objects = new ArrayList<>(Arrays.asList(images));
        final String url = "/v1/team";
        return 로그인된_상태로_파일과_함께_POST_요청을_보낸다(url, accessToken, objects, createTeamRequestDto);
    }

    public MemberFixture getFixture() {
        return memberFixture;
    }
}

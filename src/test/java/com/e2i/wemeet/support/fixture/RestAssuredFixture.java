package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.POST_요청을_보낸다;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.security.token.JwtEnv;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public enum RestAssuredFixture {
    카이(MemberFixture.KAI),
    째림(MemberFixture.RIM),
    유니(MemberFixture.SEYUN),
    워니(MemberFixture.CHAEWON),
    정열(MemberFixture.JEONGYEOL);

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
}

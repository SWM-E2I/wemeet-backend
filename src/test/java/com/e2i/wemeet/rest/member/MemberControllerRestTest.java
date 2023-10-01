package com.e2i.wemeet.rest.member;

import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.POST_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_DELETE_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_GET_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_파일과_함께_POST_요청을_보낸다;
import static com.e2i.wemeet.security.token.JwtEnv.ACCESS;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.RestAssuredFixture.카이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.rest.support.MultipartRequest;
import com.e2i.wemeet.support.module.AbstractAcceptanceTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberControllerRestTest extends AbstractAcceptanceTest {

    @DisplayName("회원 가입을 한다.")
    @Test
    void 회원가입을_한다() {
        // given
        final CreateMemberRequestDto request = KAI.createMemberRequestDto();

        // when
        final String url = "/v1/member";
        ExtractableResponse<Response> response = POST_요청을_보낸다(url, request);

        //then
        JsonPath jsonPath = response.body().jsonPath();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(200),
            () -> assertThat(response.header(ACCESS.getKey())).isNotNull(),
            () -> assertThat(jsonPath.getString("status")).isEqualTo("SUCCESS"),
            () -> assertThat(jsonPath.getString("message")).isEqualTo("Create Member Success")
        );
    }

    @DisplayName("회원 가입을 하고 유저의 상세 정보를 조회한다.")
    @Test
    void 상세정보를_조회한다() {
        // given
        final String accessToken = 카이.회원가입을_한다();

        // when
        final String url = "/v1/member";
        ExtractableResponse<Response> getResponse = 로그인된_상태로_GET_요청을_보낸다(url, accessToken);

        // then
        JsonPath jsonPath = getResponse.body().jsonPath();
        assertAll(
            () -> assertThat(getResponse.statusCode()).isEqualTo(200),
            () -> assertThat(jsonPath.getString("data.nickname")).isEqualTo(KAI.getNickname()),
            () -> assertThat(jsonPath.getBoolean("data.authUnivStatus")).isFalse()
        );
    }

    @DisplayName("로그인 하고 탈퇴한다.")
    @Test
    void 회원을_탈퇴한다() {
        // given
        final String accessToken = 카이.회원가입을_한다();

        // when
        final String url = "/v1/member";
        ExtractableResponse<Response> getResponse = 로그인된_상태로_DELETE_요청을_보낸다(url, accessToken);

        // then
        assertThat(getResponse.statusCode()).isEqualTo(200);
    }

    @DisplayName("개인 프로필 이미지를 업로드한다.")
    @Test
    void uploadProfile() throws IOException {
        // given
        final String accessToken = 카이.회원가입을_한다();
        MultipartRequest<Object> multiPartRequest = createMultiPartRequest(
            "file",
            "classpath:/static/test_image/software maestro.png"
        );

        // when
        final String url = "/v1/member/profile-image";
        ExtractableResponse<Response> getResponse = 로그인된_상태로_파일과_함께_POST_요청을_보낸다(url, accessToken, multiPartRequest);

        // then
        assertThat(getResponse.statusCode()).isEqualTo(200);
    }

}

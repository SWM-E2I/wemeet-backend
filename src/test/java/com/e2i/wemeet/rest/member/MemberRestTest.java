package com.e2i.wemeet.rest.member;

import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_DELETE_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_GET_요청을_보낸다;
import static com.e2i.wemeet.rest.support.RestAssuredRequestUtils.로그인된_상태로_파일과_함께_POST_요청을_보낸다;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.RestAssuredFixture.카이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.rest.support.MultipartRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberRestTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    ResourceLoader resourceLoader;

    @DisplayName("회원 가입을 한다.")
    @Test
    void 회원가입을_한다() {
        // given
        final CreateMemberRequestDto request = KAI.createMemberRequestDto();

        // when
        final String url = "/v1/member";
        ExtractableResponse<Response> response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .log().all()
            .when()
            .post(url)
            .then()
            .log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(200);
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
        //@Test
    void uploadProfile() throws IOException {
        // given
        final String accessToken = 카이.회원가입을_한다();
        Resource resource = resourceLoader.getResource("classpath:/static/dist/favicon-16x16.png");

        // when
        final String url = "/v1/member/profile-image";
        ExtractableResponse<Response> getResponse = 로그인된_상태로_파일과_함께_POST_요청을_보낸다(
            url, accessToken, new MultipartRequest<>("file", resource.getFile()));

        // then
        assertThat(getResponse.statusCode()).isEqualTo(200);
    }

}

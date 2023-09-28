package com.e2i.wemeet.rest.support;

import static com.e2i.wemeet.security.token.JwtEnv.ACCESS;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class RestAssuredRequestUtils {

    private RestAssuredRequestUtils() {
        throw new AssertionError();
    }

    public static <T> ExtractableResponse<Response> POST_요청을_보낸다(String url, T data) {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(data)
            .log().all()
            .when()
            .post(url)
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 로그인된_상태로_GET_요청을_보낸다(String url, String accessToken) {
        return RestAssured.given()
            .header(ACCESS.getKey(), accessToken)
            .log().all()
            .when()
            .get(url)
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 로그인된_상태로_파일과_함께_POST_요청을_보낸다(String url, String accessToken,
        MultipartRequest<Object>... multipartRequests) {
        RequestSpecification request = RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .header(ACCESS.getKey(), accessToken);

        for (MultipartRequest<Object> multipartRequest : multipartRequests) {
            request.multiPart(multipartRequest.fileName(), multipartRequest.data());
        }

        return request
            .log().all()
            .when()
            .post(url)
            .then()
            .log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> 로그인된_상태로_POST_요청을_보낸다(String url, String accessToken, T data) {
        return RestAssured.given()
            .header(ACCESS.getKey(), accessToken)
            .contentType(ContentType.JSON)
            .body(data)
            .log().all()
            .when()
            .post(url)
            .then()
            .log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> 로그인된_상태로_PUT_요청을_보낸다(String url, String accessToken, T data) {
        return RestAssured.given()
            .header(ACCESS.getKey(), accessToken)
            .contentType(ContentType.JSON)
            .body(data)
            .log().all()
            .when()
            .put(url)
            .then()
            .log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 로그인된_상태로_DELETE_요청을_보낸다(String url, String accessToken) {
        return RestAssured.given()
            .header(ACCESS.getKey(), accessToken)
            .log().all()
            .when()
            .delete(url)
            .then()
            .log().all()
            .extract();
    }

}

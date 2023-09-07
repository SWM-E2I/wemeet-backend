package com.e2i.wemeet.config.log;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LogExceptionPatternTest {

    @DisplayName("Swagger 관련 요청일 경우, true를 반환한다.")
    @CsvSource({
        "/static/dist/swagger-ui.html, true",
        "/api-docs, true",
        "/static/dist/swagger-ui.html?pattern=true, true",
        "/v1/member, false"
    })
    @ParameterizedTest
    void returnTrueWithSwaggerUrl(String requestUrl, boolean expected) {
        // given
        boolean matchedExceptionUrls = LogExceptionPattern.isMatchedExceptionUrls(requestUrl);

        // when & then
        assertThat(matchedExceptionUrls).isEqualTo(expected);
    }

}
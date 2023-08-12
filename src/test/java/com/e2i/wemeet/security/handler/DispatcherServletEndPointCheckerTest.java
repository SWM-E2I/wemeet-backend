package com.e2i.wemeet.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("DispatcherServletEndPointChecker:: 요청 핸들러 검증 테스트")
class DispatcherServletEndPointCheckerTest extends AbstractIntegrationTest {

    @DisplayName("요청에 맞는 핸들러가 있을 경우, true 를 반환한다.")
    @Test
    void isEndPointExist() {
        // given
        MockHttpServletRequest get = new MockHttpServletRequest("GET", "/v1/member");

        // when
        boolean endPointExist = httpRequestEndPointChecker.isEndPointExist(get);

        // when
        assertThat(endPointExist).isTrue();
    }

    @DisplayName("요청에 맞는 핸들러가 없을 경우, false 를 반환한다.")
    @Test
    void isEndPointDoesNotExists() {
        // given
        MockHttpServletRequest get = new MockHttpServletRequest("GET", "/invalid/url");

        // when
        boolean endPointExist = httpRequestEndPointChecker.isEndPointExist(get);

        // when
        assertThat(endPointExist).isFalse();
    }
}
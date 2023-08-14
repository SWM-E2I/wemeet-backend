package com.e2i.wemeet.security.filter;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.exception.badrequest.InvalidHttpRequestException;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

class RequestEndPointCheckFilterTest extends AbstractIntegrationTest {

    @DisplayName("요청에 맞는 핸들러가 없을 경우, 예외가 발생한다.")
    @Test
    void fail() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // when & then
        assertThatThrownBy(() -> requestEndPointCheckFilter.doFilterInternal(request, response, filterChain))
            .isExactlyInstanceOf(InvalidHttpRequestException.class);
    }

    @DisplayName("요청에 맞는 핸들러가 있을 경우, 다음 Filter 로 넘어간다.")
    @Test
    void success() {
        // given
        HttpServletRequest request = new MockHttpServletRequest("GET", "/v1/member");
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        // when & then
        assertThatNoException()
            .isThrownBy(() -> requestEndPointCheckFilter.doFilterInternal(request, response, filterChain));
    }

    @DisplayName("요청에 맞는 핸들러가 있을 경우, 요청이 정상 수행된다.")
    @Test
    void filterSuccess() throws Exception {
        // given
        ResultActions perform = mvc.perform(get("/health"));

        // when & then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$").value("Health check passed")
        );
    }

    @DisplayName("요청에 맞는 핸들러가 없을 경우, 에러 응답이 반환된다.")
    @Test
    void filterFail() throws Exception {
        // given
        ResultActions perform = mvc.perform(get("/invalid_url"));

        // when & then
        perform.andExpectAll(
            status().isNotFound(),
            jsonPath("$.status").value("ERROR"),
            jsonPath("$.code").value(40024),
            jsonPath("$.message").value("존재하지 않는 API 입니다.")
        );
    }
}
package com.e2i.wemeet.security.filter;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.exception.badrequest.InvalidHttpRequestException;
import com.e2i.wemeet.security.handler.HttpRequestEndPointChecker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.StringUtils;

class RequestEndPointCheckFilterTest {

    private RequestEndPointCheckFilter requestEndPointCheckFilter;

    @BeforeEach
    void setUp() {
        requestEndPointCheckFilter = new RequestEndPointCheckFilter(new MockHttpRequestEndPointChecker());
    }

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

    static class MockHttpRequestEndPointChecker implements HttpRequestEndPointChecker {

        @Override
        public boolean isEndPointExist(HttpServletRequest request) {
            return StringUtils.hasText(request.getRequestURI());
        }
    }
}
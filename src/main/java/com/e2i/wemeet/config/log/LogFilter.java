package com.e2i.wemeet.config.log;

import static com.e2i.wemeet.config.log.MdcKey.CORRELATION_ID;
import static com.e2i.wemeet.config.log.MdcKey.MEMBER_ID;

import com.e2i.wemeet.config.log.module.QueryCounter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LogFilter extends OncePerRequestFilter {

    private static final String REQUEST_LOG_FORMAT = "REQUEST:: HTTP_METHOD: {}, URL: {}, AGENT: {}, BODY: {}";
    private static final String RESPONSE_LOG_FORMAT = "RESPONSE:: HTTP_METHOD= {}, URL= {}, STATUS_CODE= {}, QUERY_COUNT= {}, TIME_TAKEN= {}ms, MEMBER_ID= {}, BODY= {}";
    private static final String QUERY_COUNT_WARNING_LOG_FORMAT = "WARN QUERY EXECUTION TIME:: {}";

    private static final int QUERY_COUNT_WARNING_STANDARD = 10;
    private static final String NO_REQUEST_BODY = "none";
    private static final String NOT_AUTHENTICATED = "none";

    private final StopWatch apiWatch;
    private final QueryCounter queryCounter;

    public LogFilter(final StopWatch apiWatch, final QueryCounter queryCounter) {
        this.apiWatch = apiWatch;
        this.queryCounter = queryCounter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // 요청이 Logging 제외 목록에 포함 되어있을 경우, 로그를 남기지 않고 요청 전송
        if (LogExceptionPattern.isMatchedExceptionUrls(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        final ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Before request
        addCorrelationIdToMdc();

        apiWatch.start();
        filterChain.doFilter(requestWrapper, responseWrapper);
        apiWatch.stop();

        // After request
        loggingRequest(requestWrapper);
        loggingResponse(requestWrapper, responseWrapper);
        notifyQueryCountWarning();

        MDC.clear();
        responseWrapper.copyBodyToResponse();
    }

    // 쿼리 수행 개수가 QUERY_COUNT_WARNING_STANDARD 이상일 경우, 로그를 남긴다.
    private void notifyQueryCountWarning() {
        if (getQueryCount() >= QUERY_COUNT_WARNING_STANDARD) {
            log.warn(QUERY_COUNT_WARNING_LOG_FORMAT, getQueryCount());
        }
    }

    // Request Logging
    private void loggingRequest(final ContentCachingRequestWrapper requestWrapper) {
        final String method = requestWrapper.getMethod();
        final String url = getUrlWithQueryParameters(requestWrapper);
        final String agent = requestWrapper.getHeader("User-Agent");
        final String body = new String(requestWrapper.getContentAsByteArray());

        if (!StringUtils.hasText(body)) {
            log.info(REQUEST_LOG_FORMAT, method, url, agent, NO_REQUEST_BODY);
            return;
        }

        log.info(REQUEST_LOG_FORMAT, method, url, agent, body);
    }

    // Response Logging
    private void loggingResponse(final ContentCachingRequestWrapper requestWrapper,
        final ContentCachingResponseWrapper responseWrapper) {
        final String method = requestWrapper.getMethod();
        final String url = getUrlWithQueryParameters(requestWrapper);
        final int statusCode = responseWrapper.getStatus();

        final int queryCount = getQueryCount();
        final long timeTaken = apiWatch.getTotalTimeMillis();
        final String memberId = getMemberId();
        final String body = new String(responseWrapper.getContentAsByteArray());

        log.info(RESPONSE_LOG_FORMAT, method, url, statusCode, queryCount, timeTaken, memberId, body);
    }

    // MemberId를 가져온다. (memberId가 없을 경우, 인증된 사용자가 아니므로 NOT_AUTHENTICATED를 반환한다.)
    private String getMemberId() {
        String memberId = MDC.get(MEMBER_ID.getKey());
        if (!StringUtils.hasText(memberId)) {
            return NOT_AUTHENTICATED;
        }
        return memberId;
    }

    // API 요청에서 수행된 쿼리의 개수를 가져온다.
    private int getQueryCount() {
        return queryCounter.getQueryCount();
    }

    // MDC에 correlationId를 추가한다.
    private void addCorrelationIdToMdc() {
        MDC.put(CORRELATION_ID.getKey(), String.valueOf(UUID.randomUUID()));
    }

    // 쿼리 파라미터를 포함한 URL을 가져온다.
    private String getUrlWithQueryParameters(final ContentCachingRequestWrapper requestWrapper) {
        if (!StringUtils.hasText(requestWrapper.getQueryString())) {
            return requestWrapper.getRequestURI();
        }
        return requestWrapper.getRequestURI() + "?" + requestWrapper.getQueryString();
    }


}

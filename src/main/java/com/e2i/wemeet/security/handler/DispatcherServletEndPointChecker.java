package com.e2i.wemeet.security.handler;

import static com.e2i.wemeet.config.log.OperationExcludePattern.EXCLUDE_PATTERN;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@RequiredArgsConstructor
@Component
public class DispatcherServletEndPointChecker implements HttpRequestEndPointChecker {

    private final DispatcherServlet dispatcherServlet;

    @Override
    public boolean isEndPointExist(HttpServletRequest request) {
        // check exclude request and pass
        if (StringUtils.hasText(request.getRequestURI())) {
            boolean excludeRequest = EXCLUDE_PATTERN.getExcludePattern().stream()
                .anyMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());
            if (excludeRequest) {
                return true;
            }
        }

        List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
        for (HandlerMapping handlerMapping : Objects.requireNonNull(handlerMappings)) {
            try {
                if (handlerMapping.getHandler(request) != null) {
                    return true;
                }
            } catch (Exception ignored) {
                log.warn("INVALID ENDPOINT - {}", request.getRequestURI());
                return false;
            }
        }
        log.warn("INVALID ENDPOINT - {}", request.getRequestURI());
        return false;
    }

}
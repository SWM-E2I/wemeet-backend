package com.e2i.wemeet.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
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
    private final Pattern pattern = Pattern.compile("/h2-console.*");

    @Override
    public boolean isEndPointExist(HttpServletRequest request) {
        if (StringUtils.hasText(request.getRequestURI()) && pattern.matcher(request.getRequestURI())
            .matches()) {
            return true;
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
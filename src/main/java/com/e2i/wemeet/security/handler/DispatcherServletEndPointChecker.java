package com.e2i.wemeet.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@RequiredArgsConstructor
public class DispatcherServletEndPointChecker implements HttpRequestEndPointChecker {

    private final DispatcherServlet dispatcherServlet;

    @Override
    public boolean isEndPointExist(HttpServletRequest request) {
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

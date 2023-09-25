package com.e2i.wemeet.config.log;

import java.util.List;
import java.util.regex.Pattern;

public abstract class LogExceptionPattern {

    private LogExceptionPattern() {
        throw new AssertionError();
    }

    public static final Pattern SWAGGER_UI_RESOURCE_PATTERN = Pattern.compile("^/static/dist.*");
    public static final Pattern SWAGGER_SPECIFICATION_PATTERN = Pattern.compile("^/static/swagger-ui.*");
    public static final Pattern SWAGGER_API_PATTERN = Pattern.compile("^/api-docs.*");
    public static final Pattern H2_CONSOLE_PATTERN = Pattern.compile("^/h2-console.*");

    public static final List<Pattern> LOG_EXCEPTION_LIST = List.of(
        SWAGGER_SPECIFICATION_PATTERN,
        SWAGGER_UI_RESOURCE_PATTERN,
        SWAGGER_API_PATTERN,
        H2_CONSOLE_PATTERN
    );

    public static boolean isMatchedExceptionUrls(final String requestUrl) {
        return LOG_EXCEPTION_LIST.stream()
            .anyMatch(pattern -> pattern.matcher(requestUrl).matches());
    }

}

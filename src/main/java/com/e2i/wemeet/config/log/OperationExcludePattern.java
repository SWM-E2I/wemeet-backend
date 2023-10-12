package com.e2i.wemeet.config.log;

import java.util.List;
import java.util.regex.Pattern;

public enum OperationExcludePattern {
    EXCLUDE_PATTERN(List.of(
        java.util.regex.Pattern.compile("^/static/dist.*"),
        java.util.regex.Pattern.compile("^/static/swagger-ui.*"),
        java.util.regex.Pattern.compile("^/api-docs.*"),
        java.util.regex.Pattern.compile("^/h2-console.*"),
        java.util.regex.Pattern.compile("^/actuator.*")
    ));

    private final List<Pattern> excludePattern;

    OperationExcludePattern(List<Pattern> excludePattern) {
        this.excludePattern = excludePattern;
    }

    public List<Pattern> getExcludePattern() {
        return excludePattern;
    }
}

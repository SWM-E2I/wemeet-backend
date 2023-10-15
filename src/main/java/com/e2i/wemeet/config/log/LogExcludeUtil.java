package com.e2i.wemeet.config.log;

import static com.e2i.wemeet.config.log.OperationExcludePattern.EXCLUDE_PATTERN;

public abstract class LogExcludeUtil {

    private LogExcludeUtil() {
        throw new AssertionError();
    }

    public static boolean isMatchedExcludeUrls(final String requestUrl) {
        return EXCLUDE_PATTERN.getExcludePattern().stream()
            .anyMatch(pattern -> pattern.matcher(requestUrl).matches());
    }

}

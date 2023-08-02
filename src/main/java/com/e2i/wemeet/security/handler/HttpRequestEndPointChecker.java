package com.e2i.wemeet.security.handler;

import jakarta.servlet.http.HttpServletRequest;

public interface HttpRequestEndPointChecker {

    boolean isEndPointExist(HttpServletRequest request);
}

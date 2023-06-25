package com.e2i.wemeet.config.security.token;

import java.time.Duration;

/*
* JWT 를 Header, Cookie 에서 불러올 때의 key 값과 유효 시간
* */
public enum JwtInfo {
    ACCESS("AccessToken", Duration.ofMinutes(30)),
    REFRESH("RefreshToken", Duration.ofDays(30));

    private final String key;
    private final Duration expirationTime;

    JwtInfo(String key, Duration expirationTime) {
        this.key = key;
        this.expirationTime = expirationTime;
    }

    public String getKey() {
        return key;
    }

    public long getExpirationTimeToMillis() {
        return this.expirationTime.toMillis();
    }
}

package com.e2i.wemeet.security.token;

import java.time.Duration;

/*
 * JWT 를 Header, Cookie 에서 불러올 때의 key 값과 유효 시간
 * */
public enum JwtEnv {
    ACCESS("AccessToken", Duration.ofMinutes(30)),
    REFRESH("RefreshToken", Duration.ofDays(30));

    private static final String REDIS_KEY = "memberId-%d-%s";
    private final String key;
    private final Duration expirationTime;

    JwtEnv(String key, Duration expirationTime) {
        this.key = key;
        this.expirationTime = expirationTime;
    }

    public String getKey() {
        return key;
    }

    public long getExpirationTimeToMillis() {
        return this.expirationTime.toMillis();
    }

    public static String getRedisKeyForRefresh(final Payload payload) {
        return String.format(REDIS_KEY, payload.getMemberId(), payload.getRole());
    }
}

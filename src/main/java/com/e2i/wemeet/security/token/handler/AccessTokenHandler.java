package com.e2i.wemeet.security.token.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.e2i.wemeet.security.token.JwtEnv;
import com.e2i.wemeet.security.token.Payload;
import java.util.Date;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/*
 * AccessToken 생성, 검증, 파싱 로직 수행
 * */
@Component
public class AccessTokenHandler extends TokenHandler {

    /*
     * Bearer PREFIX 를 붙여 토큰 생성
     * - memberId, role 정보 포함
     * */
    @Override
    public String createToken(final Payload payload) {
        Date expirationTime = generateExpirationTime(JwtEnv.ACCESS);

        String token = JWT.create()
            .withSubject(JwtEnv.ACCESS.name())
            .withIssuer(ISSUER)
            .withExpiresAt(expirationTime)
            .withClaim(Payload.ID, payload.getMemberId())
            .withClaim(Payload.ROLE, payload.getRole())
            .sign(Algorithm.HMAC512(secretKey));
        return ACCESS_PREFIX.concat(token);
    }

    public Payload extractToken(String accessTokenWithPrefix, boolean verify) {
        if (verify) {
            return extractToken(accessTokenWithPrefix);
        }
        return extractTokenWithNoVerify(accessTokenWithPrefix);
    }

    public Payload extractToken(String accessTokenWithPrefix) {
        String accessToken = separatePrefix(accessTokenWithPrefix);
        if (!StringUtils.hasText(accessToken)) {
            return null;
        }

        // AccessToken 검증 & 파싱
        DecodedJWT decodedJWT = decodeToken(accessToken);
        return new Payload(decodedJWT.getClaims());
    }

    public Payload extractTokenWithNoVerify(String accessTokenWithPrefix) {
        String accessToken = separatePrefix(accessTokenWithPrefix);
        if (!StringUtils.hasText(accessToken)) {
            return null;
        }

        // AccessToken 검증 & 파싱
        DecodedJWT decodedJWT = JWT.decode(accessToken);
        return new Payload(decodedJWT.getClaims());
    }

    private String separatePrefix(String accessTokenWithPrefix) {
        if (accessTokenWithPrefix != null && accessTokenWithPrefix.startsWith(ACCESS_PREFIX)) {
            return accessTokenWithPrefix.replace(ACCESS_PREFIX, "");
        }
        return null;
    }
}

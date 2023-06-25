package com.e2i.wemeet.config.security.token.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.e2i.wemeet.config.security.token.JwtInfo;
import com.e2i.wemeet.config.security.token.Payload;
import java.util.Date;
import org.springframework.stereotype.Component;

/*
* RefreshToken 생성, 검증 로직 수행
* */
@Component
public class RefreshTokenHandler extends TokenHandler {

    // Refresh Token 생성
    @Override
    public String createToken(Payload payload) {
        Date expirationTime = generateExpirationTime(JwtInfo.REFRESH);

        return JWT.create()
            .withSubject(JwtInfo.REFRESH.name())
            .withIssuer(ISSUER)
            .withExpiresAt(expirationTime)
            .sign(Algorithm.HMAC512(secretKey));
    }
}

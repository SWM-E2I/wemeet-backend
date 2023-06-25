package com.e2i.wemeet.config.security.token.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.e2i.wemeet.config.security.token.JwtInfo;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.token.JwtClaimIncorrectException;
import com.e2i.wemeet.exception.token.JwtDecodeException;
import com.e2i.wemeet.exception.token.JwtExpiredException;
import com.e2i.wemeet.exception.token.JwtSignatureMismatchException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

/*
* JWT 관련 역할 수행
* 토큰 생성 로직은 상속받는 각 클래스에서 구현
* */
public abstract class TokenHandler {
    protected static final String ACCESS_PREFIX = "Bearer ";
    protected static final String ISSUER = "WE:MEET";

    @Value("${jwt.secretKey}")
    protected String secretKey;

    protected Date generateExpirationTime(final JwtInfo jwtInfo) {
        long expiration = System.currentTimeMillis() + jwtInfo.getExpirationTimeToMillis();
        return new Date(expiration);
    }

    /*
    * JWT 를 검증 및 디코딩
    * 토큰 검증 단계에서 예외 발생시 CustomException 으로 throw
    * */
    public DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey)).build();

        try {
            // 토큰 검증
            DecodedJWT decodedJWT = verifier.verify(token);

            // 만료 기한이 지났으면 Exception 발생
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new JwtExpiredException(ErrorCode.ACCESS_TOKEN_EXPIRED);
            }

            return decodedJWT;
        } catch (AlgorithmMismatchException algorithmMismatchException) {
            throw new JwtSignatureMismatchException();
        } catch (IncorrectClaimException incorrectClaimException) {
            throw new JwtClaimIncorrectException();
        } catch (JWTDecodeException jwtDecodeException) {
            throw new JwtDecodeException();
        }
    }

    public abstract String createToken(final Payload payload);
}

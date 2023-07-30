package com.e2i.wemeet.config.security.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AccessTokenHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private AccessTokenHandler accessTokenHandler;

    @DisplayName("AccessToken 을 생성하고 생성한 토큰을 파싱하는데 성공한다.")
    @Test
    void tokenGenerateAndExtract() {
        final Payload payload = new Payload(1L, Role.USER.name());

        String token = accessTokenHandler.createToken(payload);
        Payload extractedToken = accessTokenHandler.extractToken(token);

        assertAll(
            () -> assertThat(extractedToken.getMemberId()).isEqualTo(1L),
            () -> assertThat(extractedToken.getRole()).isEqualTo(Role.USER.name())
        );
    }
}
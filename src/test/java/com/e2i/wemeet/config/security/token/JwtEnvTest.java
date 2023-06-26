package com.e2i.wemeet.config.security.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtEnvTest {

    @DisplayName("Redis 에 저장할 Key 값이 반환된다.")
    @Test
    void getRedisKeyForRefresh() {
        Payload payload = new Payload(1L, Role.USER.name());

        String redisKeyForRefresh = JwtEnv.getRedisKeyForRefresh(payload);

        String[] separate = redisKeyForRefresh.split("-");
        assertAll(
            () -> assertThat(separate[0]).isEqualTo("memberId"),
            () -> assertThat(separate[1]).isEqualTo(String.valueOf(1L)),
            () -> assertThat(separate[2]).isEqualTo(Role.USER.name())
        );
    }
}
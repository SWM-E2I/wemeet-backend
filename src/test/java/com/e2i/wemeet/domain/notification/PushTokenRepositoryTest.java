package com.e2i.wemeet.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class PushTokenRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @DisplayName("토큰 이름으로 PushToken을 조회할 수 있다.")
    @Test
    void findByToken() {
        // given
        final String tokenName = "token";
        PushToken token = PushToken.builder()
            .token(tokenName)
            .build();
        pushTokenRepository.save(token);

        // when
        Optional<PushToken> findToken = pushTokenRepository.findByToken(tokenName);

        // then
        assertThat(findToken).isPresent();
    }

}
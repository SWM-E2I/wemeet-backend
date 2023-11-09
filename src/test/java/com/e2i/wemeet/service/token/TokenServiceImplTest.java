package com.e2i.wemeet.service.token;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.notification.PushToken;
import com.e2i.wemeet.domain.notification.PushTokenRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TokenServiceImplTest extends AbstractServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @DisplayName("persist 정보를 가져오는데 성공한다.")
    @Test
    void persistLoginSuccess() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        final Long memberId = kai.getMemberId();

        PushToken token1 = PushToken.builder()
            .token("token1")
            .member(kai)
            .build();
        PushToken token2 = PushToken.builder()
            .token("token2")
            .member(kai)
            .build();
        pushTokenRepository.saveAll(List.of(token1, token2));

        // when
        PersistResponseDto persistResponseDto = tokenService.persistLogin(memberId);

        // then
        assertThat(persistResponseDto)
            .isNotNull()
            .extracting(
                "nickname", "emailAuthenticated",
                "hasMainProfileImage", "basicProfileImage",
                "lowProfileImage", "profileImageAuthenticated", "hasTeam", "pushTokens")
            .contains(KAI.getNickname(), true, true, KAI.getBasicUrl(), KAI.getLowUrl(), KAI.getImageAuth(), List.of("token1", "token2"));
    }

    @DisplayName("존재하지 않는 사용자의 persist 정보를 가져오는데 실패한다.")
    @Test
    void persistLoginFail() {
        // given
        final Long invalidMemberId = 999L;

        // when & then
        assertThatThrownBy(() -> tokenService.persistLogin(invalidMemberId))
            .isExactlyInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("push token을 저장하는데 성공한다.")
    @Test
    void savePushToken() {
        // given
        final String token = "token";
        Member member = memberRepository.save(KAI.create(ANYANG_CODE));

        // when
        tokenService.savePushToken(token, member.getMemberId());

        // then
        PushToken findToken = pushTokenRepository.findByToken(token).get();
        assertThat(findToken).isNotNull();
        assertThat(findToken.getMember()).isEqualTo(member);
    }

    @DisplayName("memberId 가 null 인 경우 push token 에 ID 가 저장되지 않는다")
    @Test
    void savePushToken_withMemberIdNull() {
        // given
        final String token = "token";
        final Long memberId = null;

        // when
        tokenService.savePushToken(token, memberId);

        // then
        PushToken findToken = pushTokenRepository.findByToken(token).get();
        assertThat(findToken).isNotNull();
        assertThat(findToken.getMember()).isNull();
    }

    @DisplayName("push token이 중복되는 경우 저장되지 않는다.")
    @Test
    void savePushToken_withDuplicateToken() {
        // given
        final String token = "token";
        Member member = memberRepository.save(KAI.create(ANYANG_CODE));
        PushToken pushToken = PushToken.builder()
            .token(token)
            .member(member)
            .build();
        pushTokenRepository.save(pushToken);

        // when
        tokenService.savePushToken(token, member.getMemberId());

        // then
        List<PushToken> findAllToken = pushTokenRepository.findAll();
        assertThat(findAllToken).hasSize(1);
    }

    @DisplayName("존재하던 push token에 ID가 업데이트 된다.")
    @Test
    void savePushToken_updateMemberId() {
        // given
        final String token = "token";
        PushToken pushToken = PushToken.builder()
            .token(token)
            .build();
        pushTokenRepository.save(pushToken);

        // when
        Long memberId = memberRepository.save(KAI.create(ANYANG_CODE)).getMemberId();
        tokenService.savePushToken(token, memberId);

        // then
        List<PushToken> findAllToken = pushTokenRepository.findAll();
        PushToken findToken = findAllToken.get(0);
        assertThat(findAllToken).hasSize(1);
        assertThat(findToken.getMember()).isNotNull();
    }
}
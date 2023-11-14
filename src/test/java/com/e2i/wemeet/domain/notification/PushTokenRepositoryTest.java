package com.e2i.wemeet.domain.notification;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PushTokenRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private PushTokenRepository pushTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @DisplayName("토큰 이름으로 PushToken을 조회할 수 있다.")
    @Test
    void findByToken() {
        // given
        final String tokenName = "token";
        PushToken token = PushToken.builder()
            .token(tokenName)
            .build();
        pushTokenRepository.save(token);

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<PushToken> findToken = pushTokenRepository.findByToken(tokenName);

        // then
        assertThat(findToken).isPresent();
    }

    @DisplayName("모든 사용자의 push token을 조회할 수 있다.")
    @Test
    void findAllMemberTokens() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));

        pushTokenRepository.save(PushToken.builder().token("token1").member(kai).build());
        pushTokenRepository.save(PushToken.builder().token("token2").member(rim).build());

        // when
        List<String> pushTokenList = pushTokenRepository.findAllMemberTokens();

        // then
        assertThat(pushTokenList).hasSize(2);
    }

    @DisplayName("모든 사용자의 push token을 조회할 때 회원가입 전의 사용자는 조회되지 않는다.")
    @Test
    void findAllMemberTokens_PreRegisteredMembersShouldBeExcluded() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));

        pushTokenRepository.save(PushToken.builder().token("token1").member(kai).build());
        pushTokenRepository.save(PushToken.builder().token("token2").member(rim).build());

        pushTokenRepository.save(PushToken.builder().token("token3").build());

        // when
        List<String> pushTokenList = pushTokenRepository.findAllMemberTokens();

        // then
        assertThat(pushTokenList).hasSize(2);
    }

    @DisplayName("팀이 없는 사용자들의 push token을 조회할 수 있다.")
    @Test
    void findTokensOfMemberWithoutTeam() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));

        teamRepository.save(
            TeamFixture.HONGDAE_TEAM_1.create(kai, TeamMemberFixture.create_3_man()));
        teamRepository.save(TeamFixture.WOMAN_TEAM.create(rim, TeamMemberFixture.create_1_woman()));

        pushTokenRepository.save(PushToken.builder().token("token1").member(kai).build());
        pushTokenRepository.save(PushToken.builder().token("token2").member(rim).build());
        pushTokenRepository.save(PushToken.builder().token("token3").member(seyun).build());

        // when
        List<String> pushTokenList = pushTokenRepository.findTokensOfMemberWithoutTeam();

        // then
        assertThat(pushTokenList).hasSize(1);
    }
}

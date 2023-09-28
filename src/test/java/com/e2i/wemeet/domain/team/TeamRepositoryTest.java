package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.KARINA;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Block;
import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BlockRepository blockRepository;

    @DisplayName("팀이 차단된 사용자의 팀인지 확인")
    @Test
    void isBlockedTeam() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member karina = memberRepository.save(KARINA.create(HANYANG_CODE));
        Member rim = memberRepository.save(MemberFixture.RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team karinaTeam = teamRepository.save(HONGDAE_TEAM_1.create(karina, create_3_woman()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        blockRepository.save(new Block(kai, karina));

        // when
        boolean isBlocked = teamRepository.isBlockedTeam(kai.getMemberId(), karinaTeam.getTeamId());
        boolean isNotBlocked = teamRepository.isBlockedTeam(kai.getMemberId(), rimTeam.getTeamId());

        // then
        assertThat(isBlocked).isTrue();
        assertThat(isNotBlocked).isFalse();
    }
}
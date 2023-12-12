package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlockRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private TeamRepository teamRepository;

    @DisplayName("차단 목록을 조회할 수 있다.")
    @Test
    void findAllByMemberId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        blockRepository.save(new Block(kai, seyun));
        blockRepository.save(new Block(kai, rim));

        // when
        List<Block> findBlock = blockRepository.findAllByMemberId(kai.getMemberId());

        // then
        assertThat(findBlock).hasSize(2)
            .extracting("member", "blockMember")
            .containsExactly(
                tuple(kai, seyun),
                tuple(kai, rim)
            );
    }

    @DisplayName("차단한 사용자 ID 목록을 조회할 수 있다.")
    @Test
    void findAllBlockMemberId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        blockRepository.save(new Block(kai, seyun));
        blockRepository.save(new Block(kai, rim));

        // when
        List<Long> findBlockMemberIds = blockRepository.findBlockMemberIds(kai.getMemberId());

        // then
        assertThat(findBlockMemberIds).hasSize(2)
            .containsExactly(seyun.getMemberId(), rim.getMemberId());
    }

    @DisplayName("내 팀 ID로 차단한 사용자 ID 목록을 조회할 수 있다.")
    @Test
    void findAllBlockMemberIdByTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        blockRepository.save(new Block(kai, seyun));
        blockRepository.save(new Block(kai, rim));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        // when
        List<Long> findBlockMemberIds = blockRepository.findBlockMemberIdsByTeamId(kaiTeam.getTeamId());

        // then
        assertThat(findBlockMemberIds).hasSize(2)
            .containsExactly(seyun.getMemberId(), rim.getMemberId());
    }

}
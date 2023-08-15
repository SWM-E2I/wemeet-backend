package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingCustomRepositoryImplTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MeetingCustomRepository meetingCustomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Nested
    class FindTeamProxy {

        @DisplayName("리더의 ID로 프록시 팀을 찾을 수 있다.")
        @Test
        void findTeamReferenceByLeaderId() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            // when
            Team proxy = meetingCustomRepository.findTeamReferenceByLeaderId(kai.getMemberId());

            // then
            assertThat(proxy).isNotNull();
        }

        @DisplayName("리더가 팀이 없다면 프록시 팀을 찾을 수 없다.")
        @Test
        void findTeamReferenceByLeaderIdWithNoTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));

            // when & then
            assertThatThrownBy(() -> meetingCustomRepository.findTeamReferenceByLeaderId(kai.getMemberId()))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

        @DisplayName("올바른 팀 ID로 프록시 팀을 찾을 수 있다.")
        @Test
        void findTeamReferenceById() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Long teamId = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man())).getTeamId();

            // when
            Team proxy = meetingCustomRepository.findTeamReferenceById(teamId);

            // then
            assertThat(proxy).isNotNull();
        }

        @DisplayName("잘못된 팀 ID로는 프록시 팀을 찾을 수 없다.")
        @Test
        void findTeamReferenceByIdWithInvalidTeamId() {
            // given
            final Long invalidTeamId = 9999L;

            // when & then
            assertThatThrownBy(() -> meetingCustomRepository.findTeamReferenceById(invalidTeamId))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

    }

}
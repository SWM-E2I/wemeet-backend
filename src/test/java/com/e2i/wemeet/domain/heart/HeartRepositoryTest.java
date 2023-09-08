package com.e2i.wemeet.domain.heart;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HeartRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private HeartRepository heartRepository;

    private static final LocalTime boundaryTime = LocalTime.of(23, 11);

    @DisplayName("이미 오늘의 좋아요를 보낸 경우 보낸 좋아요를 조회할 수 있다.")
    @Test
    void getTodayHeartStatus_WithAlreadySentHeart() {
        // given
        Member member = memberRepository.save(RIM.create(WOMANS_CODE));
        Member partner = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
        Team partnerTeam = teamRepository.save(HONGDAE_TEAM_1.create(partner, create_3_man()));

        heartRepository.save(Heart.builder()
            .team(team)
            .partnerTeam(partnerTeam)
            .build());

        entityManager.flush();
        entityManager.clear();

        LocalDateTime requestTime = LocalDateTime.now();
        LocalDateTime boundaryDateTime = requestTime.with(boundaryTime);
        if (requestTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        // when
        Optional<Heart> result = heartRepository.findTodayHeart(team.getTeamId(), boundaryDateTime,
            requestTime);

        // then
        assertThat(result).isPresent();
    }


    @DisplayName("오늘의 좋아요를 보내지 않은 경우 아무것도 조회되지 않는다.")
    @Test
    void getTodayHeartStatus_WithNotSentHeart() {
        // given
        Member member = memberRepository.save(RIM.create(WOMANS_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));

        entityManager.flush();
        entityManager.clear();

        LocalDateTime requestTime = LocalDateTime.now();
        LocalDateTime boundaryDateTime = requestTime.with(boundaryTime);
        if (requestTime.isBefore(boundaryDateTime)) {
            boundaryDateTime = boundaryDateTime.minusDays(1);
        }

        // when
        Optional<Heart> result = heartRepository.findTodayHeart(team.getTeamId(), boundaryDateTime,
            requestTime);

        // then
        assertThat(result).isNotPresent();
    }
}

package com.e2i.wemeet.domain.team_image;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamImageRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @DisplayName("팀 ID를 통해 팀 이미지를 모두 삭제할 수 있다.")
    @Test
    void deleteAllByTeamTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team));

        // when
        teamImageRepository.deleteAllByTeamTeamId(team.getTeamId());

        // then
        List<TeamImage> teamImagesByTeamId = teamImageRepository.findTeamImagesByTeamId(team.getTeamId());
        assertThat(teamImagesByTeamId).isEmpty();
    }

    @DisplayName("유저 ID로 팀을 조회할 수 있다.")
    @Test
    void findTeamByMemberId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        entityManager.flush();
        entityManager.clear();

        // when
        Team findTeam = teamImageRepository.findTeamByMemberId(kai.getMemberId())
            .orElseThrow();

        // then
        assertThat(findTeam.getTeamId()).isEqualTo(team.getTeamId());
    }

    @DisplayName("팀 ID로 팀 이미지를 조회할 수 있다.")
    @Test
    void findTeamImagesByTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        int imageSize = teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team)).size();

        // when
        List<TeamImage> findImages = teamImageRepository.findTeamImagesByTeamId(team.getTeamId());

        // then
        assertThat(findImages).hasSize(imageSize)
            .extracting("team")
            .contains(team);
    }

    @DisplayName("팀 ID로 팀 이미지의 개수를 조회할 수 있다.")
    @Test
    void countByTeamTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        int imageSize = teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team)).size();

        // when
        int findSize = teamImageRepository.countByTeamTeamId(team.getTeamId());

        // then
        assertThat(imageSize).isEqualTo(findSize);
    }

    @DisplayName("팀 이미지 URL로 팀 이미지를 모두 삭제할 수 있다.")
    @Test
    void deleteAllByTeamImageUrl() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        List<String> savedUrls = teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team))
            .stream()
            .map(TeamImage::getTeamImageUrl)
            .toList();

        // when
        teamImageRepository.deleteAllByTeamImageUrl(savedUrls);

        // then
        List<TeamImage> findImages = teamImageRepository.findTeamImagesByTeamId(team.getTeamId());
        assertThat(findImages).isEmpty();
    }

}
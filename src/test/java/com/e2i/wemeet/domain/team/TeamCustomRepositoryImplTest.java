package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.member.data.Mbti.ENFJ;
import static com.e2i.wemeet.domain.member.data.Mbti.ENFP;
import static com.e2i.wemeet.domain.member.data.Mbti.ESFJ;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TeamCustomRepositoryImplTest extends AbstractRepositoryUnitTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("팀 ID로 팀 사진 url을 조회할 수 있다.")
    @Test
    void findTeamImagesByTeamId() {
        //given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(kaiTeam));

        //when
        List<TeamImageData> teamImageUrls = teamRepository.findTeamImagesByTeamId(kaiTeam.getTeamId());

        //then
        assertThat(teamImageUrls).hasSize(3)
            .extracting(TeamImageData::url)
            .containsAll(BASIC_TEAM_IMAGE.getTeamImages());
    }

    @DisplayName("팀 사진이 등록되지 않았다면 아무것도 조회되지 않는다.")
    @Test
    void findTeamImagesByTeamIdWithoutTeamImages() {
        //given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        //when
        List<TeamImageData> teamImageUrls = teamRepository.findTeamImagesByTeamId(kaiTeam.getTeamId());

        //then
        assertThat(teamImageUrls).isEmpty();
    }

    @DisplayName("팀 ID로 팀 리더 정보를 조회할 수 있다.")
    @Test
    void findLeaderByTeamId() {
        //given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        //when
        LeaderResponseDto leader = teamRepository.findLeaderByTeamId(kaiTeam.getTeamId())
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(leader)
            .extracting("leaderId", "nickname", "collegeName", "leaderLowProfileImageUrl")
            .contains(kai.getMemberId(), kai.getNickname(), kai.getCollegeName(), kai.getProfileImage().getLowUrl());
    }

    @DisplayName("팀 ID로 팀 정보를 조회할 수 있다.")
    @Test
    void findTeamInformationByTeamId() {
        //given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kaiTeam.getTeamId())
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation)
            .extracting("teamId", "memberNum", "region", "isDeleted")
            .contains(kaiTeam.getTeamId(), kaiTeam.getMemberNum(), kaiTeam.getRegion(), kaiTeam.isDeleted());
        assertThat(teamInformation.getTeamMembers()).hasSize(3)
            .extracting("college", "collegeType", "admissionYear", "mbti")
            .contains(
                tuple("고려대", "ENGINEERING", "18", ENFJ),
                tuple("고려대", "ENGINEERING", "18", ENFP),
                tuple("인하대", "ENGINEERING", "22", ESFJ)
            );
    }
}
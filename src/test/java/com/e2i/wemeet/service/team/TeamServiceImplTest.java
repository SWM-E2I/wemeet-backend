package com.e2i.wemeet.service.team;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.KARINA;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.heart.Heart;
import com.e2i.wemeet.domain.heart.HeartRepository;
import com.e2i.wemeet.domain.member.Block;
import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.response.team.TeamDetailResponseDto;
import com.e2i.wemeet.exception.badrequest.BlockedException;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TeamServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private HeartRepository heartRepository;

    @Autowired
    private BlockRepository blockRepository;

    @DisplayName("TeamID로 Team 정보를 조회할 수 있다.")
    @Test
    void readByTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        heartRepository.save(Heart.builder()
            .team(rimTeam)
            .partnerTeam(kaiTeam)
            .build());

        teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(kaiTeam));
        final LocalDateTime readTime = LocalDateTime.now();
        setAuthentication(kai.getMemberId(), "MANAGER");

        // when
        TeamDetailResponseDto responseDto = teamService.readByTeamId(rim.getMemberId(), kaiTeam.getTeamId(), readTime);

        // then
        assertThat(responseDto)
            .extracting(
                "isDeleted", "isLiked", "memberNum", "region", "drinkRate",
                "drinkWithGame", "additionalActivity", "introduction")
            .contains(false, true, 4, Region.HONGDAE, DrinkRate.LOW,
                DrinkWithGame.ANY, AdditionalActivity.CAFE, HONGDAE_TEAM_1.getIntroduction());
        assertThat(responseDto.teamImageUrls())
            .hasSize(3)
            .containsAll(BASIC_TEAM_IMAGE.getTeamImages());
        assertThat(responseDto.teamMembers())
            .hasSize(3)
            .extracting("college", "collegeType", "admissionYear", "mbti")
            .containsExactly(
                tuple("고려대", CollegeType.ENGINEERING.name(), "18", Mbti.ENFJ),
                tuple("고려대", CollegeType.ENGINEERING.name(), "18", Mbti.ENFP),
                tuple("인하대", CollegeType.ENGINEERING.name(), "22", Mbti.ESFJ)
            );
        assertThat(responseDto.leader())
            .extracting("leaderId", "nickname", "collegeName",
                "collegeType", "leaderLowProfileImageUrl")
            .contains(kai.getMemberId(), kai.getNickname(), kai.getCollegeName(),
                kai.getCollegeInfo().getCollegeType(), kai.getProfileImage().getLowUrl());
    }

    @DisplayName("차단된 사용자의 팀은 조회할 수 없다.")
    @Test
    void readByTeamId_withBlockTeam() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member karina = memberRepository.save(KARINA.create(HANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team karinaTeam = teamRepository.save(HONGDAE_TEAM_1.create(karina, create_3_woman()));

        blockRepository.save(new Block(kai, karina));
        LocalDateTime requestTime = LocalDateTime.now();

        // when & then
        assertThatThrownBy(() -> teamService.readByTeamId(kai.getMemberId(), karinaTeam.getTeamId(), requestTime))
            .isExactlyInstanceOf(BlockedException.class);
    }
}
package com.e2i.wemeet.domain.heart;

import static com.e2i.wemeet.support.fixture.MemberFixture.JEONGYEOL;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.member.Block;
import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.dsl.HeartTeamData;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HeartCustomRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    private HeartCustomRepositoryImpl heartCustomRepository;

    @Autowired
    private HeartRepository heartRepository;

    @Autowired
    private BlockRepository blockRepository;

    @DisplayName("보낸 좋아요 조회 테스트")
    @Nested
    class GetSentHeart {

        @DisplayName("오늘 좋아요를 보낸 상대 팀 정보를 조회할 수 있다.")
        @Test
        void getSentHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner = memberRepository.save(KAI.create(ANYANG_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam = teamRepository.save(HONGDAE_TEAM_1.create(partner, create_3_man()));

            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam));

            heartRepository.save(Heart.builder()
                .team(team)
                .partnerTeam(partnerTeam)
                .build());

            //when
            List<HeartTeamData> result = heartCustomRepository.findSentHeart(team.getTeamId(),
                LocalDateTime.now());

            // then
            assertThat(result).hasSize(1)
                .extracting("teamId", "memberNum", "region", "teamMainImageUrl",
                    "teamLeader.nickname",
                    "teamLeader.mbti", "teamLeader.profileImageUrl", "teamLeader.college")
                .contains(
                    tuple(partnerTeam.getTeamId(), partnerTeam.getMemberNum(),
                        partnerTeam.getRegion(),
                        BASIC_TEAM_IMAGE.getTeamImages().get(0),
                        partnerTeam.getTeamLeader().getNickname(),
                        partnerTeam.getTeamLeader().getMbti(),
                        partnerTeam.getTeamLeader().getProfileImage().getBasicUrl(),
                        partnerTeam.getTeamLeader().getCollegeInfo().getCollegeCode()
                            .getCodeValue())
                );
        }

        @DisplayName("오늘 좋아요를 보내지 않은 경우 아무것도 조회되지 않는다.")
        @Test
        void getSentHeart_WithNotSentHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));

            // when
            List<HeartTeamData> result = heartCustomRepository.findSentHeart(team.getTeamId(),
                LocalDateTime.now());

            // then
            assertThat(result).isEmpty();
        }

        @DisplayName("차단된 사용자는 보낸 좋아요 목록에 조회되지 않는다.")
        @Test
        void getReceivedHeartWithoutBlockedMember() {
            // given
            Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
            Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
            Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
            Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));

            saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);
            saveHeartEntity(rimTeam, jeonyeolTeam, seyunTeam);

            blockRepository.save(new Block(rim, seyun));
            LocalDateTime requestTime = LocalDateTime.now();

            // when
            List<HeartTeamData> receivedHeart = heartCustomRepository.findSentHeart(rimTeam.getTeamId(), requestTime);

            // then
            assertThat(receivedHeart).hasSize(1)
                .extracting("teamId")
                .containsExactly(jeonyeolTeam.getTeamId());
        }
    }

    @DisplayName("받은 좋아요 조회 테스트")
    @Nested
    class GetReceivedHeart {

        @DisplayName("오늘 좋아요를 받은 상대 팀 정보를 조회할 수 있다.")
        @Test
        void getReceivedHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner1 = memberRepository.save(KAI.create(ANYANG_CODE));
            Member partner2 = memberRepository.save(SEYUN.create(ANYANG_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam1 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner1, create_3_man()));
            Team partnerTeam2 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner2, create_3_man()));

            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam1));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam2));

            saveHeartEntity(partnerTeam1, team);
            saveHeartEntity(partnerTeam2, team);

            // when
            List<HeartTeamData> result = heartCustomRepository.findReceivedHeart(team.getTeamId(),
                LocalDateTime.now());

            // then
            assertThat(result).hasSize(2)
                .extracting("teamId", "memberNum", "region", "teamMainImageUrl",
                    "teamLeader.nickname",
                    "teamLeader.mbti", "teamLeader.profileImageUrl", "teamLeader.college")
                .contains(
                    tuple(partnerTeam1.getTeamId(), partnerTeam1.getMemberNum(),
                        partnerTeam1.getRegion(),
                        BASIC_TEAM_IMAGE.getTeamImages().get(0),
                        partnerTeam1.getTeamLeader().getNickname(),
                        partnerTeam1.getTeamLeader().getMbti(),
                        partnerTeam1.getTeamLeader().getProfileImage().getBasicUrl(),
                        partnerTeam1.getTeamLeader().getCollegeInfo().getCollegeCode()
                            .getCodeValue()),
                    tuple(partnerTeam2.getTeamId(), partnerTeam2.getMemberNum(),
                        partnerTeam2.getRegion(),
                        BASIC_TEAM_IMAGE.getTeamImages().get(0),
                        partnerTeam2.getTeamLeader().getNickname(),
                        partnerTeam2.getTeamLeader().getMbti(),
                        partnerTeam2.getTeamLeader().getProfileImage().getBasicUrl(),
                        partnerTeam2.getTeamLeader().getCollegeInfo().getCollegeCode()
                            .getCodeValue())
                );
        }


        @DisplayName("오늘 받은 좋아요가 없는 경우 아무것도 조회되지 않는다.")
        @Test
        void getReceivedHeart_WithNotReceivedHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));

            // when
            List<HeartTeamData> result = heartCustomRepository.findReceivedHeart(team.getTeamId(),
                LocalDateTime.now());

            // then
            assertThat(result).isEmpty();
        }

        @DisplayName("차단된 사용자는 받은 좋아요 목록에 조회되지 않는다.")
        @Test
        void getReceivedHeartWithoutBlockedMember() {
            // given
            Member rim = memberRepository.save(RIM.create(ANYANG_CODE));
            Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
            Member jeongyeol = memberRepository.save(JEONGYEOL.create(HANYANG_CODE));

            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team jeonyeolTeam = teamRepository.save(HONGDAE_TEAM_1.create(jeongyeol, create_3_man()));
            Team seyunTeam = teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));
            saveTeamImages(rimTeam, jeonyeolTeam, seyunTeam);

            saveHeartEntity(jeonyeolTeam, rimTeam);
            saveHeartEntity(seyunTeam, rimTeam);

            blockRepository.save(new Block(rim, seyun));
            LocalDateTime requestTime = LocalDateTime.now();

            // when
            List<HeartTeamData> receivedHeart = heartCustomRepository.findReceivedHeart(rimTeam.getTeamId(), requestTime);

            // then
            assertThat(receivedHeart).hasSize(1)
                .extracting("teamId")
                .containsExactly(jeonyeolTeam.getTeamId());
        }
    }

    private void saveHeartEntity(Team team, Team... partnerTeam) {
        for (Team partner : partnerTeam) {
            heartRepository.save(Heart.builder()
                .team(team)
                .partnerTeam(partner)
                .build());
        }
    }

    private void saveTeamImages(Team... teams) {
        for (Team team : teams) {
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(team));
        }
    }
}


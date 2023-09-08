package com.e2i.wemeet.service.heart;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.domain.heart.Heart;
import com.e2i.wemeet.domain.heart.HeartRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.response.heart.ReceivedHeartResponseDto;
import com.e2i.wemeet.dto.response.heart.SentHeartResponseDto;
import com.e2i.wemeet.exception.badrequest.HeartAlreadyExistsException;
import com.e2i.wemeet.exception.badrequest.NotSendToOwnTeamException;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HeartServiceTest extends AbstractServiceTest {

    @Autowired
    private HeartService heartService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;
    @Autowired
    private HeartRepository heartRepository;


    @DisplayName("좋아요 보내기 테스트")
    @Nested
    class sendHeartTest {

        @DisplayName("상대 팀에게 좋아요를 보낼 수 있다.")
        @Test
        void sendHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner = memberRepository.save(KAI.create(ANYANG_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam = teamRepository.save(HONGDAE_TEAM_1.create(partner, create_3_man()));

            setAuthentication(member.getMemberId(), "MANAGER");
            LocalDateTime requestTime = LocalDateTime.now();

            // when
            heartService.sendHeart(member.getMemberId(), partnerTeam.getTeamId(), requestTime);

            // then
            List<Heart> result = heartRepository.findAll();
            assertThat(result).hasSize(1)
                .extracting("team", "partnerTeam")
                .contains(
                    tuple(team, partnerTeam)
                );
        }


        @DisplayName("오늘 좋아요를 이미 소진한 경우, 좋아요를 보낼 수 없다.")
        @Test
        void sendHeart_AlreadySentTodayHeart() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner1 = memberRepository.save(KAI.create(ANYANG_CODE));
            Member partner2 = memberRepository.save(SEYUN.create(KOREA_CODE));
            setAuthentication(member.getMemberId(), "MANAGER");

            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam1 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner1, create_3_man()));
            Team partnerTeam2 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner2, create_3_man()));

            heartRepository.save(Heart.builder()
                .team(team)
                .partnerTeam(partnerTeam1)
                .build());

            // when & then
            assertThatThrownBy(
                () -> heartService.sendHeart(member.getMemberId(), partnerTeam2.getTeamId(),
                    LocalDateTime.now()))
                .isExactlyInstanceOf(HeartAlreadyExistsException.class);
        }

        @DisplayName("자신의 팀에게는 좋아요를 보낼 수 없다.")
        @Test
        void sendHeart_ToOwnTeam() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            setAuthentication(member.getMemberId(), "MANAGER");

            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));

            // when & then
            assertThatThrownBy(
                () -> heartService.sendHeart(member.getMemberId(), team.getTeamId(),
                    LocalDateTime.now()))
                .isExactlyInstanceOf(NotSendToOwnTeamException.class);
        }

        @DisplayName("좋아요를 보낼 상대 팀이 없는 경우, 좋아요를 보낼 수 없다.")
        @Test
        void sendHeart_NotExistedPartnerTeam() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            setAuthentication(member.getMemberId(), "MANAGER");

            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));

            // when & then
            assertThatThrownBy(
                () -> heartService.sendHeart(member.getMemberId(), 2L,
                    LocalDateTime.now()))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

        @DisplayName("본인 팀이 없는 경우, 좋아요를 보낼 수 없다.")
        @Test
        void sendHeart_WithoutTeam() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));

            // when & then
            assertThatThrownBy(
                () -> heartService.sendHeart(member.getMemberId(), 2L,
                    LocalDateTime.now()))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }
    }

    @DisplayName("보낸 좋아요 조회 테스트")
    @Nested
    class getSentHeartTest {

        @DisplayName("보낸 좋아요 내역을 조회할 수 있다.")
        @Test
        void getSentHeart() {
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner = memberRepository.save(KAI.create(ANYANG_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam = teamRepository.save(HONGDAE_TEAM_1.create(partner, create_3_man()));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam));

            setAuthentication(member.getMemberId(), "MANAGER");

            heartRepository.save(Heart.builder()
                .team(team)
                .partnerTeam(partnerTeam)
                .build());

            // when
            SentHeartResponseDto result = heartService.getSentHeart(member.getMemberId(),
                LocalDateTime.now());

            // then
            assertThat(result).isNotNull();
            assertThat(result.teamId()).isEqualTo(partnerTeam.getTeamId());
            assertThat(result.memberNum()).isEqualTo(partnerTeam.getMemberNum());
            assertThat(result.region()).isEqualTo(partnerTeam.getRegion().getName());
            assertThat(result.profileImageURL()).isEqualTo(partner.getProfileImage().getBasicUrl());
            assertThat(result.mainImageURL()).isEqualTo(BASIC_TEAM_IMAGE.getTeamImages().get(0));
            assertThat(result.sentTime()).isNotNull();
            assertThat(result.leader()).isNotNull();
            assertThat(result.leader().nickname()).isEqualTo(partner.getNickname());
            assertThat(result.leader().mbti()).isEqualTo(partner.getMbti().name());
            assertThat(result.leader().college()).isEqualTo(
                partner.getCollegeInfo().getCollegeCode().getCodeValue());
        }

        @DisplayName("본인 팀이 없는 경우, 보낸 좋아요 내역을 조회할 수 없다.")
        @Test
        void getSentHeart_WithoutTeam() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));

            // when & then
            assertThatThrownBy(
                () -> heartService.getSentHeart(member.getMemberId(), LocalDateTime.now()))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }
    }

    @DisplayName("받은 좋아요 조회 테스트")
    @Nested
    class getReceivedHeartTest {

        @DisplayName("받은 좋아요 내역을 조회할 수 있다.")
        @Test
        void getReceivedHeart() {
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));
            Member partner1 = memberRepository.save(KAI.create(ANYANG_CODE));
            Member partner2 = memberRepository.save(SEYUN.create(KOREA_CODE));
            Team team = teamRepository.save(HONGDAE_TEAM_1.create(member, create_3_woman()));
            Team partnerTeam1 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner1, create_3_man()));
            Team partnerTeam2 = teamRepository.save(
                HONGDAE_TEAM_1.create(partner2, create_3_man()));

            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam1));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(partnerTeam2));

            setAuthentication(member.getMemberId(), "MANAGER");

            heartRepository.save(Heart.builder()
                .team(partnerTeam1)
                .partnerTeam(team)
                .build());

            heartRepository.save(Heart.builder()
                .team(partnerTeam2)
                .partnerTeam(team)
                .build());

            // when
            List<ReceivedHeartResponseDto> result = heartService.getReceivedHeart(
                member.getMemberId(), LocalDateTime.now());

            // then
            assertThat(result).hasSize(2)
                .extracting("teamId", "memberNum", "region", "mainImageURL", "profileImageURL",
                    "leader.nickname", "leader.college", "leader.mbti")
                .contains(
                    Tuple.tuple(partnerTeam1.getTeamId(), partnerTeam1.getMemberNum(),
                        partnerTeam1.getRegion().getName(),
                        BASIC_TEAM_IMAGE.getTeamImages().get(0),
                        partner1.getProfileImage().getBasicUrl(), partner1.getNickname(),
                        partner1.getCollegeInfo().getCollegeCode().getCodeValue(),
                        partner1.getMbti().name()
                    ),
                    Tuple.tuple(partnerTeam2.getTeamId(), partnerTeam2.getMemberNum(),
                        partnerTeam2.getRegion().getName(),
                        BASIC_TEAM_IMAGE.getTeamImages().get(0),
                        partner2.getProfileImage().getBasicUrl(), partner2.getNickname(),
                        partner2.getCollegeInfo().getCollegeCode().getCodeValue(),
                        partner2.getMbti().name()
                    )
                );
        }

        @DisplayName("본인 팀이 없는 경우, 보낸 좋아요 내역을 조회할 수 없다.")
        @Test
        void getReceivedHeart_WithoutTeam() {
            // given
            Member member = memberRepository.save(RIM.create(WOMANS_CODE));

            // when & then
            assertThatThrownBy(
                () -> heartService.getReceivedHeart(member.getMemberId(), LocalDateTime.now()))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }
    }
}
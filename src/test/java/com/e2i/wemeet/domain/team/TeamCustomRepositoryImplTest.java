package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.member.data.Mbti.ENFJ;
import static com.e2i.wemeet.domain.member.data.Mbti.ENFP;
import static com.e2i.wemeet.domain.member.data.Mbti.ESFJ;
import static com.e2i.wemeet.support.config.ReflectionUtils.setFieldValueToSuperClassField;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.heart.Heart;
import com.e2i.wemeet.domain.heart.HeartRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    @Autowired
    private HeartRepository heartRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

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
        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), kaiTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation)
            .extracting("teamId", "memberNum", "region", "isDeleted", "isLiked", "chatLink")
            .contains(kaiTeam.getTeamId(), kaiTeam.getMemberNum(), kaiTeam.getRegion(), kaiTeam.isDeleted(), false, HONGDAE_TEAM_1.getChatLink());
        assertThat(teamInformation.getTeamMembers()).hasSize(3)
            .extracting("college", "collegeType", "admissionYear", "mbti")
            .contains(
                tuple("고려대", "ENGINEERING", "18", ENFJ),
                tuple("고려대", "ENGINEERING", "18", ENFP),
                tuple("인하대", "ENGINEERING", "22", ESFJ)
            );
    }

    @DisplayName("좋아요 내역을 조회할 수 있다.")
    @Test
    void findHeart() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        heartRepository.save(Heart.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build());
        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), rimTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation.getIsLiked()).isTrue();
    }

    @DisplayName("좋아요를 여러번 눌렀어도, 해당 좋아요 이력들이 유효한 기간이 지났다면 isLiked 가 false 가 된다")
    @Test
    void findHeartRecent() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        Heart heart1 = heartRepository.save(Heart.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build());
        setFieldValueToSuperClassField(heart1, "createdAt", LocalDateTime.now().minusDays(10));
        Heart heart2 = heartRepository.save(Heart.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build());
        setFieldValueToSuperClassField(heart2, "createdAt", LocalDateTime.now().minusDays(11));
        LocalDateTime readTime = LocalDateTime.now();
        entityManager.flush();
        entityManager.clear();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), rimTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation.getIsLiked()).isFalse();
    }

    @DisplayName("미팅 신청 현황을 조회할 수 있다.")
    @EnumSource
    @ParameterizedTest
    void findMeetingRequestStatus(AcceptStatus acceptStatus) {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build();
        meetingRequest.changeStatus(acceptStatus);
        meetingRequestRepository.save(meetingRequest);
        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), rimTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation.getMeetingRequestStatus()).isEqualTo(acceptStatus);
    }

    @DisplayName("가장 최신의 미팅 신청 현황을 조회할 수 있다.")
    @Test
    void findMeetingRequestExpired() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        MeetingRequest meetingRequest2 = MeetingRequest.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build();
        meetingRequest2.changeStatus(EXPIRED);
        meetingRequestRepository.save(meetingRequest2);
        setFieldValueToSuperClassField(meetingRequest2, "createdAt", LocalDateTime.now().minusDays(10));

        MeetingRequest meetingRequest = MeetingRequest.builder()
            .team(kaiTeam)
            .partnerTeam(rimTeam)
            .build();
        meetingRequest.changeStatus(ACCEPT);
        meetingRequestRepository.save(meetingRequest);

        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), rimTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation.getMeetingRequestStatus()).isEqualTo(ACCEPT);
    }

    @DisplayName("미팅 신청내역이 없을 경우 null이 반환된다.")
    @Test
    void findMeetingRequestStatusWithoutRequest() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(kai.getMemberId(), rimTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        //then
        assertThat(teamInformation.getMeetingRequestStatus()).isNull();
    }

    @DisplayName("팀이 없어도 다른 팀 상세 정보를 조회할 수 있다.")
    @Test
    void findTeamInformationWithoutTeam() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        LocalDateTime readTime = LocalDateTime.now();

        //when
        TeamInformationDto teamInformation = teamRepository.findTeamInformationByTeamId(rim.getMemberId(), kaiTeam.getTeamId(), readTime)
            .orElseThrow(TeamNotFoundException::new);

        // then
        assertThat(teamInformation)
            .extracting("teamId", "memberNum", "region", "isDeleted", "isLiked")
            .contains(kaiTeam.getTeamId(), kaiTeam.getMemberNum(), kaiTeam.getRegion(), kaiTeam.isDeleted(), false);
        assertThat(teamInformation.getTeamMembers()).hasSize(3)
            .extracting("college", "collegeType", "admissionYear", "mbti")
            .contains(
                tuple("고려대", "ENGINEERING", "18", ENFJ),
                tuple("고려대", "ENGINEERING", "18", ENFP),
                tuple("인하대", "ENGINEERING", "22", ESFJ)
            );
    }
}
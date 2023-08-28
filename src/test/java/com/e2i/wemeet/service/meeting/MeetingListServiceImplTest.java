package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.domain.team.data.Region.HONGDAE;
import static com.e2i.wemeet.service.meeting.MeetingListServiceImpl.MEETING_EXPIRE_DAY;
import static com.e2i.wemeet.support.fixture.MeetingFixture.BASIC_MEETING;
import static com.e2i.wemeet.support.fixture.MeetingFixture.SECOND_MEETING;
import static com.e2i.wemeet.support.fixture.MemberFixture.CHAEWON;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.support.config.ReflectionUtils;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetingListServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingListService meetingListService;

    @Nested
    class AcceptedMeetingListTest {

        @DisplayName("성사된 미팅 목록을 조회할 수 있다.")
        @Test
        void getAcceptedMeetingList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            meetingRepository.save(SECOND_MEETING.create(kaiTeam, chaewonTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).hasSize(2)
                .extracting("region", "leader.nickname", "isExpired")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), false),
                    tuple(HONGDAE, CHAEWON.getNickname(), false)
                );
        }

        @DisplayName("만료된 미팅 목록도 함께 조회된다.")
        @Test
        void getAcceptedMeetingListContainsExpiredMeeting() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 미팅 시간
            final LocalDateTime meetingAcceptTime = findDateTime.minusDays(MEETING_EXPIRE_DAY).minusMinutes(1);

            Meeting rimMeeting = meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            meetingRepository.save(SECOND_MEETING.create(kaiTeam, chaewonTeam));

            // 만료된 미팅 시간으로 변경
            ReflectionUtils.setFieldValueToSuperClassField(rimMeeting, "createdAt", meetingAcceptTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).hasSize(2)
                .extracting("region", "leader.nickname", "isExpired")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), true),
                    tuple(HONGDAE, CHAEWON.getNickname(), false)
                );
        }

        @DisplayName("미팅 목록 조회 시, 만료된 미팅 데이터 상태가 업데이트 된다.")
        @Test
        void getAcceptedMeetingListUpdateExpiredData() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 미팅 시간
            final LocalDateTime meetingAcceptTime = findDateTime.minusDays(MEETING_EXPIRE_DAY).minusMinutes(1);

            Meeting rimMeeting = meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            final List<Long> meetingIdList = List.of(
                meetingRepository.save(SECOND_MEETING.create(kaiTeam, chaewonTeam)).getMeetingId(),
                rimMeeting.getMeetingId()
            );

            // 만료된 미팅 시간으로 변경
            ReflectionUtils.setFieldValueToSuperClassField(rimMeeting, "createdAt", meetingAcceptTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            List<Meeting> meetings = meetingRepository.findByMeetingIdIn(meetingIdList);
            assertThat(meetings).hasSize(2)
                .extracting("isOver")
                .containsExactlyInAnyOrder(true, false);
        }

        @DisplayName("미팅 목록 조회 시, 상대 팀이 삭제 되었을 경우 미팅 데이터 상태가 업데이트 된다.")
        @Test
        void getAcceptedMeetingListUpdateDeletedTeamMeetingData() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime teamDeleteTime = LocalDateTime.now();

            Meeting rimMeeting = meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            final List<Long> meetingIdList = List.of(
                meetingRepository.save(SECOND_MEETING.create(kaiTeam, chaewonTeam)).getMeetingId(),
                rimMeeting.getMeetingId()
            );

            // 팀 삭제
            rimTeam.delete(teamDeleteTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            List<Meeting> meetings = meetingRepository.findByMeetingIdIn(meetingIdList);
            assertThat(meetings).hasSize(2)
                .extracting("isOver")
                .containsExactlyInAnyOrder(true, false);
        }

        @DisplayName("미팅에 성사된 상대 팀이 삭제된 상태여도 조회가 가능하다")
        @Test
        void getAcceptedMeetingListWithDeletedPartnerTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRepository.saveAll(List.of(
                BASIC_MEETING.create(kaiTeam, rimTeam),
                SECOND_MEETING.create(kaiTeam, chaewonTeam)
            ));

            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime teamDeleteTime = LocalDateTime.now();
            // 팀 삭제
            rimTeam.delete(teamDeleteTime);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).hasSize(2)
                .extracting("region", "leader.nickname", "isExpired")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), true),
                    tuple(HONGDAE, CHAEWON.getNickname(), false)
                );
        }

        @DisplayName("사용자의 삭제된 예전 팀 미팅 성사 목록은 조회되지 않는다.")
        @Test
        void getAcceptedMeetingListWithDeletedTeamAndNotDeletedTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            // 첫번째 팀 미팅 성사 후 삭제
            meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            final LocalDateTime teamDeleteTime = LocalDateTime.now();
            kaiTeam.delete(teamDeleteTime);

            // 두번째 팀 미팅 성사
            final Team kaiSecondTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            meetingRepository.save(SECOND_MEETING.create(kaiSecondTeam, chaewonTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).hasSize(1)
                .extracting("region", "leader.nickname", "isExpired")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), false)
                );
        }

        @DisplayName("소속 되어있던 팀이 삭제되었다면, 아무 내용도 조회되지 않는다.")
        @Test
        void getAcceptedMeetingListWithHasNoDeletedTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            // 첫번째 팀 미팅 성사 후 삭제
            meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            final LocalDateTime teamDeleteTime = LocalDateTime.now();
            kaiTeam.delete(teamDeleteTime);

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 팀이 삭제되었을 경우, Role 이 USER로 변경되지만 로직상 MANAGER 권한이 필요하므로 MANAGER 권한 부여
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).isEmpty();
        }

        @DisplayName("성사된 미팅이 없다면 아무 내용도 조회되지 않는다.")
        @Test
        void getAcceptedMeetingListWithoutAnyMeeting() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingListService.getAcceptedMeetingList(kai.getMemberId(), findDateTime);

            // then
            assertThat(acceptedMeetingList).isEmpty();
        }

    }

    @Nested
    class SentMeetingListTest {

    }

    @Nested
    class ReceivedMeetingListTest {

    }

}
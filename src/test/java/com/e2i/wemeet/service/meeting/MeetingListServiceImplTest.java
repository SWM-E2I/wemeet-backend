package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.REJECT;
import static com.e2i.wemeet.domain.team.data.Region.HONGDAE;
import static com.e2i.wemeet.service.meeting.MeetingListServiceImpl.MEETING_EXPIRE_DAY;
import static com.e2i.wemeet.service.meeting.MeetingListServiceImpl.REQUEST_EXPIRE_DAY;
import static com.e2i.wemeet.support.fixture.MeetingFixture.BASIC_MEETING;
import static com.e2i.wemeet.support.fixture.MeetingFixture.SECOND_MEETING;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.BASIC_REQUEST;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.WITH_OUT_MESSAGE;
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
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
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

        @DisplayName("보낸 미팅 신청 요청 목록을 조회할 수 있다.")
        @Test
        void getSentRequestList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRequestRepository.saveAll(List.of(
                WITH_OUT_MESSAGE.create(kaiTeam, rimTeam),
                BASIC_REQUEST.create(kaiTeam, chaewonTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(2)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), null),
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("유효 기간이 지난 신청은 조회되지 않는다.")
        @Test
        void getSentRequestListWithExpiredRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            MeetingRequest kaiRimRequest = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam));
            meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, chaewonTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 요청 시간
            final LocalDateTime requestSentTime = findDateTime.minusDays(REQUEST_EXPIRE_DAY).minusMinutes(1);
            ReflectionUtils.setFieldValueToSuperClassField(kaiRimRequest, "createdAt", requestSentTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("유효 기간이 지난 신청은 EXPIRED 상태로 변경된다.")
        @Test
        void getSentRequestListUpdateExpiredRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            MeetingRequest kaiRimRequest = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 요청 시간
            final LocalDateTime requestSentTime = findDateTime.minusDays(REQUEST_EXPIRE_DAY).minusMinutes(1);
            ReflectionUtils.setFieldValueToSuperClassField(kaiRimRequest, "createdAt", requestSentTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            final Long expiredMeetingRequestId = kaiRimRequest.getMeetingRequestId();

            // when
            meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            MeetingRequest expiredMeetingRequest = meetingRequestRepository.findById(expiredMeetingRequestId).orElseThrow();
            assertThat(expiredMeetingRequest.getAcceptStatus()).isEqualTo(EXPIRED);
        }

        @DisplayName("거절된 신청은 조회되지 않는다.")
        @Test
        void getSentRequestListWithRejectedRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final MeetingRequest kaiRimRequest = WITH_OUT_MESSAGE.create(kaiTeam, rimTeam);
            kaiRimRequest.changeStatus(REJECT);
            meetingRequestRepository.saveAll(List.of(
                kaiRimRequest,
                BASIC_REQUEST.create(kaiTeam, chaewonTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("수락된 신청은 조회되지 않는다.")
        @Test
        void getSentRequestListWithAcceptedRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final MeetingRequest kaiRimRequest = WITH_OUT_MESSAGE.create(kaiTeam, rimTeam);
            kaiRimRequest.changeStatus(ACCEPT);
            meetingRequestRepository.saveAll(List.of(
                kaiRimRequest,
                BASIC_REQUEST.create(kaiTeam, chaewonTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("신청을 보낸 상대 팀이 삭제되었을 경우 조회되지 않는다.")
        @Test
        void getSentRequestListWithPartnerTeamHasDeleted() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRequestRepository.saveAll(List.of(
                WITH_OUT_MESSAGE.create(kaiTeam, rimTeam),
                BASIC_REQUEST.create(kaiTeam, chaewonTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime deleteTime = LocalDateTime.now();
            chaewonTeam.delete(deleteTime);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), null)
                );
        }

        @DisplayName("신청을 보낸 상대 팀이 삭제되었을 경우 신청 상태가 EXPIRED로 변경된다.")
        @Test
        void getSentRequestListWithPartnerTeamHasDeletedUpdateExpired() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            Long expiredRequestId = meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, chaewonTeam)).getMeetingRequestId();
            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime deleteTime = LocalDateTime.now();
            chaewonTeam.delete(deleteTime);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            MeetingRequest meetingRequest = meetingRequestRepository.findById(expiredRequestId).orElseThrow();
            assertThat(meetingRequest.getAcceptStatus()).isEqualTo(EXPIRED);
        }

        @DisplayName("팀이 삭제되었을 경우, 삭제되기 전에 보낸 신청 이력을 조회할 수 없다.")
        @Test
        void getSentRequestListAfterDeletedTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            // 첫 번째 팀에서 요청을 보낸 후 삭제
            meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam));
            final LocalDateTime deleteTime = LocalDateTime.now();
            kaiTeam.delete(deleteTime);

            // 두 번째 팀에서 요청 보냄
            Team kaiSecondTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            meetingRequestRepository.save(BASIC_REQUEST.create(kaiSecondTeam, chaewonTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("보낸 신청이 없을 경우, 아무 신청도 조회되지 않는다.")
        @Test
        void getSentRequestListWithoutRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingListService.getSentRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(sentRequestList).isEmpty();
        }

    }

    @Nested
    class ReceivedMeetingListTest {

        @DisplayName("받은 미팅 신청 요청 목록을 조회할 수 있다.")
        @Test
        void getReceiveRequestList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRequestRepository.saveAll(List.of(
                WITH_OUT_MESSAGE.create(rimTeam, kaiTeam),
                BASIC_REQUEST.create(chaewonTeam, kaiTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(2)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), null),
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("유효 기간이 지난 신청은 조회되지 않는다.")
        @Test
        void getReceiveRequestListWithExpiredRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            MeetingRequest kaiRimRequest = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(rimTeam, kaiTeam));
            meetingRequestRepository.save(BASIC_REQUEST.create(chaewonTeam, kaiTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 요청 시간
            final LocalDateTime requestSentTime = findDateTime.minusDays(REQUEST_EXPIRE_DAY).minusMinutes(1);
            ReflectionUtils.setFieldValueToSuperClassField(kaiRimRequest, "createdAt", requestSentTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("유효 기간이 지난 받은 신청은 EXPIRED 상태로 변경된다.")
        @Test
        void getReceiveRequestListUpdateExpiredRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            MeetingRequest kaiRimRequest = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(rimTeam, kaiTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            // 만료된 요청 시간
            final LocalDateTime requestSentTime = findDateTime.minusDays(REQUEST_EXPIRE_DAY).minusMinutes(1);
            ReflectionUtils.setFieldValueToSuperClassField(kaiRimRequest, "createdAt", requestSentTime);
            entityManager.flush();
            entityManager.clear();
            setAuthentication(kai.getMemberId(), "MANAGER");

            final Long expiredMeetingRequestId = kaiRimRequest.getMeetingRequestId();

            // when
            meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            MeetingRequest expiredMeetingRequest = meetingRequestRepository.findById(expiredMeetingRequestId).orElseThrow();
            assertThat(expiredMeetingRequest.getAcceptStatus()).isEqualTo(EXPIRED);
        }

        @DisplayName("거절한 신청은 조회되지 않는다.")
        @Test
        void getReceiveRequestListWithRejectedRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final MeetingRequest kaiRimRequest = WITH_OUT_MESSAGE.create(rimTeam, kaiTeam);
            kaiRimRequest.changeStatus(REJECT);
            meetingRequestRepository.saveAll(List.of(
                kaiRimRequest,
                BASIC_REQUEST.create(chaewonTeam, kaiTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("수락한 신청은 조회되지 않는다.")
        @Test
        void getReceiveRequestListWithAcceptedRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            final MeetingRequest kaiRimRequest = WITH_OUT_MESSAGE.create(rimTeam, kaiTeam);
            kaiRimRequest.changeStatus(ACCEPT);
            meetingRequestRepository.saveAll(List.of(
                kaiRimRequest,
                BASIC_REQUEST.create(chaewonTeam, kaiTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("신청을 보낸 상대 팀이 삭제되었을 경우 해당 신청 건이 조회되지 않는다.")
        @Test
        void getReceiveRequestListWithPartnerTeamHasDeleted() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            meetingRequestRepository.saveAll(List.of(
                WITH_OUT_MESSAGE.create(rimTeam, kaiTeam),
                BASIC_REQUEST.create(chaewonTeam, kaiTeam))
            );
            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime deleteTime = LocalDateTime.now();
            chaewonTeam.delete(deleteTime);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, RIM.getNickname(), null)
                );
        }

        @DisplayName("신청을 보낸 상대 팀이 삭제되었을 경우 신청 상태가 EXPIRED로 변경된다.")
        @Test
        void getReceiveRequestListWithPartnerTeamHasDeletedUpdateExpired() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            Long expiredRequestId = meetingRequestRepository.save(BASIC_REQUEST.create(chaewonTeam, kaiTeam)).getMeetingRequestId();
            final LocalDateTime findDateTime = LocalDateTime.now();
            final LocalDateTime deleteTime = LocalDateTime.now();
            chaewonTeam.delete(deleteTime);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            MeetingRequest meetingRequest = meetingRequestRepository.findById(expiredRequestId).orElseThrow();
            assertThat(meetingRequest.getAcceptStatus()).isEqualTo(EXPIRED);
        }

        @DisplayName("팀이 삭제되었을 경우, 삭제되기 전에 받은 신청 이력을 조회할 수 없다.")
        @Test
        void getSentRequestListAfterDeletedTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            // 첫 번째 팀에서 미팅 신청 받은 후 삭제
            meetingRequestRepository.save(WITH_OUT_MESSAGE.create(rimTeam, kaiTeam));
            final LocalDateTime deleteTime = LocalDateTime.now();
            kaiTeam.delete(deleteTime);

            // 두 번째 팀에서 미팅 신청을 받음
            Team kaiSecondTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            meetingRequestRepository.save(BASIC_REQUEST.create(chaewonTeam, kaiSecondTeam));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).hasSize(1)
                .extracting("region", "leader.nickname", "message")
                .contains(
                    tuple(HONGDAE, CHAEWON.getNickname(), BASIC_REQUEST.getMessage())
                );
        }

        @DisplayName("받은 신청이 없을 경우, 아무것도 조회되지 않는다.")
        @Test
        void receivedRequestsWithoutRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            final LocalDateTime findDateTime = LocalDateTime.now();
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            List<ReceivedMeetingResponseDto> receivedRequests = meetingListService.getReceiveRequestList(kai.getMemberId(), findDateTime);

            // then
            assertThat(receivedRequests).isEmpty();
        }

    }

}
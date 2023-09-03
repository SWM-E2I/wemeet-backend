package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.REJECT;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.BASIC_REQUEST;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.dto.request.meeting.SendMeetingWithMessageRequestDto;
import com.e2i.wemeet.exception.badrequest.BadRequestException;
import com.e2i.wemeet.exception.badrequest.ExpiredException;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.exception.badrequest.TeamHasBeenDeletedException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetingHandleServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MeetingHandleService meetingHandleService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("미팅 신청 테스트")
    @Nested
    class SendRequest {

        @DisplayName("미팅을 신청할 수 있다.")
        @Test
        void sendRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            SendMeetingRequestDto request = new SendMeetingRequestDto(rimTeam.getTeamId());
            setAuthentication(kai.getMemberId(), "MANAGER");

            final int kaiCredit = kai.getCredit();

            // when
            meetingHandleService.sendRequest(request, kai.getMemberId());

            // then
            List<MeetingRequest> meetingRequests = meetingRequestRepository.findAll();
            assertThat(meetingRequests).hasSize(1)
                .extracting("team", "partnerTeam", "acceptStatus", "message")
                .contains(
                    tuple(kaiTeam, rimTeam, AcceptStatus.PENDING, null)
                );
            assertThat(kai.getCredit()).isLessThan(kaiCredit);
        }

        @DisplayName("보유하고 있는 크레딧이 부족하면 미팅을 신청할 수 없다.")
        @Test
        void sendRequestWithNotEnoughCredit() {
            // given
            final int credit = 1;
            Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            SendMeetingRequestDto request = new SendMeetingRequestDto(rimTeam.getTeamId());
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(CreditNotEnoughException.class);
        }

        @DisplayName("미팅을 신청하면 크레딧이 차감된다.")
        @Test
        void sendRequestReduceCreditAfterRequest() {
            // given
            final int credit = 50;
            Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            SendMeetingRequestDto request = new SendMeetingRequestDto(rimTeam.getTeamId());
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingHandleService.sendRequest(request, kai.getMemberId());

            // then
            Integer findCredit = memberRepository.findCreditByMemberId(kai.getMemberId())
                .orElseThrow();
            assertThat(findCredit).isLessThan(credit);
        }

        @DisplayName("partnerTeamId가 잘못되었다면 미팅을 신청할 수 없다.")
        @Test
        void sendRequestWithInvalidPartnerTeamId() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            SendMeetingRequestDto request = new SendMeetingRequestDto(999L);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

        @DisplayName("미팅을 신청하는 팀이 삭제되었다면 미팅을 신청할 수 없다.")
        @Test
        void sendRequestToHasBeenDeletedTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            rimTeam.delete(LocalDateTime.of(2023, 8, 15, 13, 0));

            SendMeetingRequestDto request = new SendMeetingRequestDto(rimTeam.getTeamId());
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(TeamHasBeenDeletedException.class);
        }

        @DisplayName("팀이 없는 사용자라면 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithoutTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            SendMeetingRequestDto request = new SendMeetingRequestDto(rimTeam.getTeamId());
            setAuthentication(kai.getMemberId(), "USER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(UnAuthorizedRoleException.class);
        }

    }

    @DisplayName("쪽지와 함께 미팅 신청 테스트")
    @Nested
    class SendRequestWithMessage {

        @DisplayName("쪽지와 함께 미팅을 신청할 수 있다.")
        @Test
        void sendResponseWithMessage() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final String message = "안녕하세요";
            final int kaiCredit = kai.getCredit();

            SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(rimTeam.getTeamId(), message);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingHandleService.sendRequestWithMessage(request, kai.getMemberId());

            // then
            List<MeetingRequest> meetingRequests = meetingRequestRepository.findAll();
            assertThat(meetingRequests).hasSize(1)
                .extracting("team", "partnerTeam", "acceptStatus", "message")
                .contains(
                    tuple(kaiTeam, rimTeam, AcceptStatus.PENDING, message)
                );
            assertThat(kai.getCredit()).isLessThan(kaiCredit);
        }

        @DisplayName("보유하고 있는 크레딧이 부족하면 쪽지와 함께 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithNotEnoughCredit() {
            // given
            final int credit = 1;
            Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final String message = "안녕하세요";

            SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(rimTeam.getTeamId(), message);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequestWithMessage(request, kai.getMemberId()))
                .isExactlyInstanceOf(CreditNotEnoughException.class);
        }

        @DisplayName("미팅을 신청하면 크레딧이 차감된다.")
        @Test
        void sendResponseWithMessageReduceCreditAfterRequest() {
            // given
            final int credit = 50;
            Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final String message = "안녕하세요";

            SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(rimTeam.getTeamId(), message);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when
            meetingHandleService.sendRequestWithMessage(request, kai.getMemberId());

            // then
            Integer findCredit = memberRepository.findCreditByMemberId(kai.getMemberId())
                .orElseThrow();
            assertThat(findCredit).isLessThan(credit);
        }

        @DisplayName("팀이 없는 사용자라면 쪽지와 함께 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithoutTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final String message = "안녕하세요";

            SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(rimTeam.getTeamId(), message);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequestWithMessage(request, kai.getMemberId()))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

        @DisplayName("쪽지의 길이가 50자 이상이라면 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithLongMessage() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final String message = "안녕하세요 저는 ~~ 살고있는 ~~ 입니다. 저희는 높은 텐션을 가지고 있어서 재밌게 놀 수 있을 것 같아요!";

            SendMeetingWithMessageRequestDto request = new SendMeetingWithMessageRequestDto(rimTeam.getTeamId(), message);
            setAuthentication(kai.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.sendRequestWithMessage(request, kai.getMemberId()))
                .isExactlyInstanceOf(UndeclaredThrowableException.class);
        }

    }

    @DisplayName("미팅 신청 수락/거절 테스트")
    @Nested
    class HandleRequest {

        @DisplayName("받았던 미팅 신청을 수락할 수 있다.")
        @Test
        void handleRequestToAccept() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final Long meetingRequestId = meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, rimTeam))
                .getMeetingRequestId();
            final String chatLink = "https://open.kakao.com/o/1sdfasdf";
            final LocalDateTime now = LocalDateTime.now();

            setAuthentication(rim.getMemberId(), "MANAGER");
            final int rimCredit = rim.getCredit();

            // when
            Long meetingId = meetingHandleService.acceptRequest(chatLink, rim.getMemberId(), meetingRequestId, now);

            // then
            MeetingRequest findRequest = meetingRequestRepository.findById(meetingRequestId)
                .orElseThrow();
            Meeting findMeeting = meetingRepository.findById(meetingId)
                .orElseThrow();

            assertAll(
                () -> assertThat(findRequest).isNotNull()
                    .extracting("acceptStatus", "message")
                    .contains(ACCEPT, BASIC_REQUEST.getMessage()),
                () -> assertThat(findMeeting).isNotNull()
                    .extracting("team", "partnerTeam", "chatLink", "isOver")
                    .contains(kaiTeam, rimTeam, chatLink, false));

            assertThat(rim.getCredit()).isLessThan(rimCredit);
        }

        @DisplayName("미팅 신청을 수락하면 크레딧이 차감된다.")
        @Test
        void handleRequestToAcceptReduceCreditAfterAccept() {
            // given
            final int credit = 30;
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create_credit(WOMANS_CODE, credit));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final Long meetingRequestId = meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, rimTeam))
                .getMeetingRequestId();
            final String chatLink = "https://open.kakao.com/o/1sdfasdf";
            final LocalDateTime now = LocalDateTime.now();

            setAuthentication(rim.getMemberId(), "MANAGER");

            // when
            meetingHandleService.acceptRequest(chatLink, rim.getMemberId(), meetingRequestId, now);

            // then
            Integer findCredit = memberRepository.findCreditByMemberId(rim.getMemberId())
                .orElseThrow();
            assertThat(findCredit).isLessThan(credit);
        }

        @DisplayName("받았던 미팅 신청을 거절할 수 있다.")
        @Test
        void handleRequestToReject() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            MeetingRequest meetingRequest = BASIC_REQUEST.create(kaiTeam, rimTeam);
            final Long meetingRequestId = meetingRequestRepository.save(meetingRequest).getMeetingRequestId();
            final LocalDateTime rejectDateTime = LocalDateTime.now();
            setAuthentication(rim.getMemberId(), "MANAGER");

            // when
            AcceptStatus acceptStatus = meetingHandleService.rejectRequest(rim.getMemberId(), meetingRequestId, rejectDateTime);

            // then
            MeetingRequest findRequest = meetingRequestRepository.findById(meetingRequestId).get();
            Optional<Meeting> findMeeting = meetingRepository.findByTeamId(kaiTeam.getTeamId());

            assertAll(
                () -> assertThat(acceptStatus).isEqualTo(REJECT),
                () -> assertThat(findRequest).isNotNull()
                    .extracting("acceptStatus", "message")
                    .contains(REJECT, BASIC_REQUEST.getMessage()),
                () -> assertThat(findMeeting).isEmpty()
            );
        }

        @DisplayName("카카오톡 오픈 채팅방 링크를 입력하지 않으면 미팅 신청을 수락할 수 없다.")
        @Test
        void handleRequestToAcceptWithoutChatLink() {
            // given
            final String chatLink = null;

            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final Long meetingRequestId = meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, rimTeam))
                .getMeetingRequestId();
            final LocalDateTime acceptDateTime = LocalDateTime.now();
            setAuthentication(rim.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.acceptRequest(chatLink, rim.getMemberId(), meetingRequestId, acceptDateTime))
                .isExactlyInstanceOf(InvalidDataFormatException.class);
        }

        @DisplayName("미팅 신청을 받은 대상이 아니라면 미팅 신청 상태를 변경할 수 없다.")
        @Test
        void handleRequestNotReceived() {
            // given
            final Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));

            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            teamRepository.save(HONGDAE_TEAM_1.create(seyun, create_3_man()));

            MeetingRequest meetingRequest = BASIC_REQUEST.create(kaiTeam, rimTeam);
            final Long meetingRequestId = meetingRequestRepository.save(meetingRequest).getMeetingRequestId();
            final LocalDateTime rejectDateTime = LocalDateTime.now();
            setAuthentication(rim.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.rejectRequest(seyun.getMemberId(), meetingRequestId, rejectDateTime))
                .isExactlyInstanceOf(BadRequestException.class);
        }

        @DisplayName("팀이 없는 사용자는 미팅 신청 상태를 변경할 수 없다.")
        @Test
        void handleRequestWithoutTeam() {
            // given
            final Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));

            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            MeetingRequest meetingRequest = BASIC_REQUEST.create(kaiTeam, rimTeam);

            final LocalDateTime rejectDateTime = LocalDateTime.now();
            final Long meetingRequestId = meetingRequestRepository.save(meetingRequest).getMeetingRequestId();
            setAuthentication(rim.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.rejectRequest(seyun.getMemberId(), meetingRequestId, rejectDateTime))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

        @DisplayName("미팅 요청이 3일 이상 되었다면 미팅 신청 상태를 변경할 수 없다.")
        @Test
        void handleRequestWithExpiredRequest() {
            // given
            final String chatLink = null;

            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

            final MeetingRequest meetingRequest = meetingRequestRepository.save(BASIC_REQUEST.create(kaiTeam, rimTeam));
            final LocalDateTime now = LocalDateTime.of(2023, 8, 10, 14, 0, 0);
            setAuthentication(rim.getMemberId(), "MANAGER");

            // when & then
            assertThatThrownBy(() -> meetingHandleService.acceptRequest(chatLink, rim.getMemberId(), meetingRequest.getMeetingRequestId(), now))
                .isExactlyInstanceOf(ExpiredException.class);

            assertThat(meetingRequest.getAcceptStatus()).isEqualTo(EXPIRED);
        }

    }

}
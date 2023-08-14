package com.e2i.wemeet.service.meeting;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.dto.request.meeting.SendMeetingRequestDto;
import com.e2i.wemeet.exception.badrequest.TeamHasBeenDeletedException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MeetingServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

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
            setAuthentication(kai.getMemberId(), "USER");

            // when
            meetingService.sendRequest(request, kai.getMemberId());

            // then
            List<MeetingRequest> meetingRequests = meetingRequestRepository.findAll();
            assertThat(meetingRequests).hasSize(1)
                .extracting("team", "partnerTeam", "acceptStatus")
                .contains(
                    tuple(kaiTeam, rimTeam, AcceptStatus.PENDING)
                );
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
            setAuthentication(kai.getMemberId(), "USER");

            // when & then
            assertThatThrownBy(() -> meetingService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(CreditNotEnoughException.class);
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
            setAuthentication(kai.getMemberId(), "USER");

            // when & then
            assertThatThrownBy(() -> meetingService.sendRequest(request, kai.getMemberId()))
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
            setAuthentication(kai.getMemberId(), "USER");

            // when & then
            assertThatThrownBy(() -> meetingService.sendRequest(request, kai.getMemberId()))
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
            assertThatThrownBy(() -> meetingService.sendRequest(request, kai.getMemberId()))
                .isExactlyInstanceOf(TeamNotFoundException.class);
        }

    }

    @Nested
    class SendRequestWithMessage {

        @DisplayName("쪽지와 함께 미팅을 신청할 수 있다.")
        @Test
        void sendResponseWithMessage() {
            // given

            // when

            // then

        }

        @DisplayName("보유하고 있는 크레딧이 부족하면 쪽지와 함께 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithNotEnoughCredit() {
            // given

            // when

            // then

        }

        @DisplayName("팀이 없는 사용자라면 쪽지와 함께 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithoutTeam() {
            // given

            // when

            // then

        }

        @DisplayName("쪽지의 길이가 50자 이상이라면 미팅을 신청할 수 없다.")
        @Test
        void sendResponseWithMessageWithLongMessage() {
            // given

            // when

            // then

        }

    }

    @Nested
    class HandleRequest {

        @DisplayName("받았던 미팅 신청을 수락할 수 있다.")
        @Test
        void handleRequestToAccept() {
            // given

            // when

            // then
        }

        @DisplayName("받았던 미팅 신청을 거절할 수 있다.")
        @Test
        void handleRequestToReject() {
            // given

            // when

            // then
        }

        @DisplayName("카카오톡 오픈 채팅방 링크를 입력하지 않으면 미팅 신청을 수락할 수 없다.")
        @Test
        void handleRequestToAcceptWithoutChatLink() {
            // given

            // when

            // then
        }

        @DisplayName("미팅 신청을 받은 대상이 아니라면 미팅 신청 상태를 변경할 수 없다.")
        @Test
        void handleRequestNotReceived() {
            // given

            // when

            // then
        }

        @DisplayName("팀이 없는 사용자는 미팅 신청 상태를 변경할 수 없다.")
        @Test
        void handleRequestWithoutTeam() {
            // given

            // when

            // then
        }

    }

    @Nested
    class GetRequestList {

        @DisplayName("보낸 미팅 신청 목록을 조회할 수 있다.")
        @Test
        void getSentRequestList() {
            // given

            // when

            // then
        }

        @DisplayName("받은 미팅 신청 목록을 조회할 수 있다.")
        @Test
        void getReceivedRequestList() {
            // given

            // when

            // then
        }

    }

    private void setAuthentication(Long memberId, String role) {
        MemberPrincipal principal = new MemberPrincipal(memberId, role);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
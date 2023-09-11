package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.ACCEPT;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.REJECT;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.BASIC_REQUEST;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.WITH_OUT_MESSAGE;
import static com.e2i.wemeet.support.fixture.MemberFixture.CHAEWON;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.meeting.data.AcceptStatus;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingRequestRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @DisplayName("여러건의 미팅 요청을 만료 처리 할 수 있다.")
    @Test
    void updateRequestToExpired() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

        List<Long> list = meetingRequestRepository.saveAll(List.of(
            WITH_OUT_MESSAGE.create(kaiTeam, rimTeam),
            BASIC_REQUEST.create(kaiTeam, chaewonTeam))
        ).stream().map(MeetingRequest::getMeetingRequestId).toList();

        // when
        meetingRequestRepository.updateRequestToExpired(list);

        // then
        List<MeetingRequest> findMeetingRequest = meetingRequestRepository.findAllById(list);
        assertThat(findMeetingRequest).hasSize(2)
            .extracting("acceptStatus")
            .containsOnly(EXPIRED);
    }

    @DisplayName("미팅 신청이 이미 존재하는지 확인할 수 있다.")
    @Test
    void findMeetingRequestByTeamAndPartnerTeamId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam));

        // when
        Optional<Long> findId = meetingRequestRepository.findIdByTeamIdAndPartnerTeamId(kaiTeam.getTeamId(), rimTeam.getTeamId());

        // then
        assertThat(findId).isPresent();
    }

    @DisplayName("미팅을 신청하지 않았다면 아무것도 조회되지 않는다.")
    @Test
    void findMeetingRequestByTeamAndPartnerTeamIdWithoutRequest() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        // when
        Optional<Long> findId = meetingRequestRepository.findIdByTeamIdAndPartnerTeamId(kaiTeam.getTeamId(), rimTeam.getTeamId());

        // then
        assertThat(findId).isEmpty();
    }

    @DisplayName("이전에 미팅을 신청했던 팀이더라도, 해당 요청이 현재 '대기중' 상태가 아니라면 아무것도 조회되지 않는다.")
    @Test
    void findMeetingRequestByTeamAndPartnerTeamIdBeforeRequestExpired() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        MeetingRequest meetingRequest = WITH_OUT_MESSAGE.create(kaiTeam, rimTeam);
        meetingRequest.changeStatus(REJECT);
        meetingRequestRepository.save(meetingRequest);

        // when
        Optional<Long> findId = meetingRequestRepository.findIdByTeamIdAndPartnerTeamId(kaiTeam.getTeamId(), rimTeam.getTeamId());

        // then
        assertThat(findId).isEmpty();
    }

    @DisplayName("팀의 미팅 신청 목록을 조회할 수 있다.")
    @CsvSource({"ACCEPT, 1", "PENDING, 1", "REJECT, 0", "EXPIRED, 0"})
    @ParameterizedTest
    void findAllByTeamAndAcceptStatus(final AcceptStatus status, final int expectedSize) {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

        MeetingRequest meetingRequestAccept = WITH_OUT_MESSAGE.create(kaiTeam, rimTeam);
        meetingRequestAccept.changeStatus(ACCEPT);
        MeetingRequest meetingRequestPending = BASIC_REQUEST.create(kaiTeam, chaewonTeam);

        meetingRequestRepository.saveAll(List.of(meetingRequestAccept, meetingRequestPending));

        // when
        List<MeetingRequest> findMeetingRequest = meetingRequestRepository.findAllByTeamAndAcceptStatus(kaiTeam, status);

        // then
        assertThat(findMeetingRequest).hasSize(expectedSize);
    }
}
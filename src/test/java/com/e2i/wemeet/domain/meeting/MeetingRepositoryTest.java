package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.support.fixture.MeetingFixture.BASIC_MEETING;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.WITH_OUT_MESSAGE;
import static com.e2i.wemeet.support.fixture.MemberFixture.CHAEWON;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @DisplayName("미팅 상태를 '종료'로 변경할 수 있다.")
    @Test
    void updateMeetingToOver() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
        Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

        List<Long> meetingIdList = List.of(
            meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam)).getMeetingId(),
            meetingRepository.save(BASIC_MEETING.create(kaiTeam, chaewonTeam)).getMeetingId()
        );

        // when
        meetingRepository.updateMeetingToOver(meetingIdList);
        entityManager.flush();
        entityManager.clear();

        // then
        List<Meeting> meetingList = meetingRepository.findAllById(meetingIdList);
        assertThat(meetingList).hasSize(2)
            .extracting(Meeting::getIsOver)
            .contains(true, true);
    }

    @DisplayName("팀장의 핸드폰 번호를 조회할 수 있다.")
    @Test
    void findTeamLeaderPhoneNumber() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        teamRepository.delete(kaiTeam);
        Team kaiTeamSecond = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        // when
        String leaderPhoneNumber = meetingRepository.findLeaderPhoneNumberById(kaiTeamSecond.getTeamId());

        // then
        assertThat(leaderPhoneNumber).isEqualTo(KAI.getPhoneNumber());
    }

    @DisplayName("미팅 요청 정보를 통해 이전 미팅 성사 내역을 조회할 수 있다")
    @Test
    void findIdByLeaderIdAndMeetingRequestId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam)).getMeetingRequestId();
        Meeting meeting = meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
        Long meetingRequestId2 = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam)).getMeetingRequestId();
        Meeting meeting2 = meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
        entityManager.flush();
        entityManager.clear();

        // when
        List<LocalDateTime> findMeetingCreatedAt = meetingRepository.findCreatedAtByMeetingRequestId(meetingRequestId2);

        // then
        assertThat(findMeetingCreatedAt).hasSize(2);
    }

    @DisplayName("미팅 성사 내역이 없다면 이전 미팅 성사 내역을 조회할 수 없다.")
    @Test
    void findIdByLeaderIdAndMeetingRequestIdWithoutMeeting() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
        Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));

        meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam)).getMeetingRequestId();
        Long meetingRequestId2 = meetingRequestRepository.save(WITH_OUT_MESSAGE.create(kaiTeam, rimTeam)).getMeetingRequestId();
        entityManager.flush();
        entityManager.clear();

        // when
        List<LocalDateTime> findMeetingCreatedAt = meetingRepository.findCreatedAtByMeetingRequestId(meetingRequestId2);

        // then
        assertThat(findMeetingCreatedAt).isEmpty();
    }

}
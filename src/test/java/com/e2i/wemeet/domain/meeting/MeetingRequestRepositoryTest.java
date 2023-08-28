package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.domain.meeting.data.AcceptStatus.EXPIRED;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.BASIC_REQUEST;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.WITH_OUT_MESSAGE;
import static com.e2i.wemeet.support.fixture.MemberFixture.CHAEWON;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        Assertions.assertThat(findMeetingRequest).hasSize(2)
            .extracting("acceptStatus")
            .containsOnly(EXPIRED);
    }
}
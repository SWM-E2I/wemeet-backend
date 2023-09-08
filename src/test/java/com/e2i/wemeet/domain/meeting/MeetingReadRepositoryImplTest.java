package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.support.fixture.MeetingFixture.BASIC_MEETING;
import static com.e2i.wemeet.support.fixture.MeetingRequestFixture.BASIC_REQUEST;
import static com.e2i.wemeet.support.fixture.MemberFixture.CHAEWON;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.BASIC_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamImagesFixture.SECOND_TEAM_IMAGE;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.dto.response.meeting.AcceptedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingReadRepositoryImplTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MeetingReadRepository meetingReadRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MeetingRequestRepository meetingRequestRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Nested
    class FindTeamProxy {

        @DisplayName("리더의 ID로 프록시 팀을 찾을 수 있다.")
        @Test
        void findTeamReferenceByLeaderId() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            // when
            Team proxy = meetingReadRepository.findTeamReferenceByLeaderId(kai.getMemberId());

            // then
            assertThat(proxy).isNotNull();
        }

        @DisplayName("리더가 팀이 없다면 프록시 팀을 찾을 수 없다.")
        @Test
        void findTeamReferenceByLeaderIdWithNoTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));

            // when & then
            assertThatThrownBy(() -> meetingReadRepository.findTeamReferenceByLeaderId(kai.getMemberId()))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }

        @DisplayName("올바른 팀 ID로 프록시 팀을 찾을 수 있다.")
        @Test
        void findTeamReferenceById() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Long teamId = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man())).getTeamId();

            // when
            Team proxy = meetingReadRepository.findTeamReferenceById(teamId);

            // then
            assertThat(proxy).isNotNull();
        }

        @DisplayName("잘못된 팀 ID로는 프록시 팀을 찾을 수 없다.")
        @Test
        void findTeamReferenceByIdWithInvalidTeamId() {
            // given
            final Long invalidTeamId = 9999L;

            // when & then
            assertThatThrownBy(() -> meetingReadRepository.findTeamReferenceById(invalidTeamId))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }

        @DisplayName("리더의 ID로 팀 ID를 찾을 수 있다.")
        @Test
        void findTeamIdByLeaderId() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Long teamId = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man())).getTeamId();

            // when
            Long findTeamId = meetingReadRepository.findTeamIdByLeaderId(kai.getMemberId());

            // then
            assertThat(teamId).isEqualTo(findTeamId);
        }

        @DisplayName("잘못된 리더의 ID로는 팀 ID를 찾을 수 없다.")
        @Test
        void findTeamIdByInvalidLeaderId() {
            // given
            final Long invalidLeaderId = 9999L;
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Long teamId = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man())).getTeamId();

            // when
            assertThatThrownBy(() -> meetingReadRepository.findTeamIdByLeaderId(invalidLeaderId))
                .isExactlyInstanceOf(TeamNotExistsException.class);
        }

    }

    @DisplayName("미팅 신청 목록 조회 테스트")
    @Nested
    class FindMeeting {

        @DisplayName("성사된 미팅 목록을 조회할 수 있다.")
        @Test
        void findAcceptedMeetingList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));

            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(rimTeam));
            teamImageRepository.saveAll(SECOND_TEAM_IMAGE.createTeamImages(chaewonTeam));

            meetingRepository.save(BASIC_MEETING.create(kaiTeam, rimTeam));
            meetingRepository.save(BASIC_MEETING.create(kaiTeam, chaewonTeam));

            // when
            List<AcceptedMeetingResponseDto> meetingList = meetingReadRepository.findAcceptedMeetingList(kai.getMemberId());

            // then
            assertThat(meetingList).hasSize(2)
                .extracting("memberCount", "region", "isDeleted",
                    "teamProfileImageUrl", "leader.nickname")
                .contains(
                    tuple(4, rimTeam.getRegion(), false,
                        BASIC_TEAM_IMAGE.getTeamImages(), rim.getNickname()
                    ),
                    tuple(4, chaewonTeam.getRegion(), false,
                        SECOND_TEAM_IMAGE.getTeamImages(), chaewon.getNickname()
                    )
                );

        }

        @DisplayName("성사된 미팅이 없을 경우, 아무것도 조회되지 않는다.")
        @Test
        void findAcceptedMeetingListWithNoneSuccessedMeeting() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingReadRepository.findAcceptedMeetingList(kai.getMemberId());

            // then
            assertThat(acceptedMeetingList).isEmpty();
        }

        @DisplayName("팀이 없을 경우 아무것도 조회되지 않는다.")
        @Test
        void findAcceptedMeetingListWithNoTeam() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));

            // when
            List<AcceptedMeetingResponseDto> acceptedMeetingList = meetingReadRepository.findAcceptedMeetingList(kai.getMemberId());

            // then
            assertThat(acceptedMeetingList).isEmpty();
        }

        @DisplayName("보낸 요청 목록을 조회할 수 있다.")
        @Test
        void findSentRequestList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(rimTeam));
            teamImageRepository.saveAll(SECOND_TEAM_IMAGE.createTeamImages(chaewonTeam));

            meetingRequestRepository.saveAll(List.of(
                BASIC_REQUEST.create(kaiTeam, rimTeam),
                BASIC_REQUEST.create(kaiTeam, chaewonTeam))
            );

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingReadRepository.findSentRequestList(kai.getMemberId());

            // then
            assertThat(sentRequestList).hasSize(2)
                .extracting("memberCount", "region", "partnerTeamDeleted",
                    "teamProfileImageUrl", "leader.nickname")
                .contains(
                    tuple(4, rimTeam.getRegion(), false,
                        BASIC_TEAM_IMAGE.getTeamImages(), rim.getNickname()
                    ),
                    tuple(4, chaewonTeam.getRegion(), false,
                        SECOND_TEAM_IMAGE.getTeamImages(), chaewon.getNickname()
                    )
                );
        }

        @DisplayName("보낸 요청이 없을 경우, 아무것도 조회되지 않는다.")
        @Test
        void findSentRequestListWithNoRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            // when
            List<SentMeetingResponseDto> sentRequestList = meetingReadRepository.findSentRequestList(kai.getMemberId());

            // then
            assertThat(sentRequestList).isEmpty();
        }

        @DisplayName("받은 요청을 목록을 조회할 수 있다.")
        @Test
        void findReceivedRequestList() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            Member rim = memberRepository.save(RIM.create(WOMANS_CODE));
            Member chaewon = memberRepository.save(CHAEWON.create(WOMANS_CODE));

            Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));
            Team rimTeam = teamRepository.save(HONGDAE_TEAM_1.create(rim, create_3_woman()));
            Team chaewonTeam = teamRepository.save(HONGDAE_TEAM_1.create(chaewon, create_3_woman()));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(rimTeam));
            teamImageRepository.saveAll(BASIC_TEAM_IMAGE.createTeamImages(chaewonTeam));

            meetingRequestRepository.saveAll(List.of(
                BASIC_REQUEST.create(rimTeam, kaiTeam),
                BASIC_REQUEST.create(chaewonTeam, kaiTeam))
            );

            // when
            List<ReceivedMeetingResponseDto> receivedRequest = meetingReadRepository.findReceiveRequestList(kai.getMemberId());

            // then
            assertThat(receivedRequest).hasSize(2)
                .extracting("memberCount", "region", "partnerTeamDeleted",
                    "teamProfileImageUrl", "leader.nickname")
                .contains(
                    tuple(4, rimTeam.getRegion(), false,
                        BASIC_TEAM_IMAGE.getTeamImages(), rim.getNickname()
                    ),
                    tuple(4, chaewonTeam.getRegion(), false,
                        BASIC_TEAM_IMAGE.getTeamImages(), chaewon.getNickname()
                    )
                );
        }

        @DisplayName("받은 요청이 없을 경우, 아무것도 조회되지 않는다.")
        @Test
        void findReceivedRequestListWithNoRequest() {
            // given
            Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
            teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

            // when
            List<ReceivedMeetingResponseDto> receivedRequest = meetingReadRepository.findReceiveRequestList(kai.getMemberId());

            // then
            assertThat(receivedRequest).isEmpty();
        }

    }

}
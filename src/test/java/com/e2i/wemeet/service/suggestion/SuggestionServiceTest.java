package com.e2i.wemeet.service.suggestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.history.HistoryRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionHistoryData;
import com.e2i.wemeet.domain.team.data.suggestion.SuggestionTeamData;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import com.e2i.wemeet.dto.response.suggestion.CheckSuggestionResponseDto;
import com.e2i.wemeet.dto.response.suggestion.SuggestionResponseDto;
import com.e2i.wemeet.exception.badrequest.SuggestionHistoryExistsException;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.fixture.TeamFixture;
import com.e2i.wemeet.support.fixture.TeamMemberFixture;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private SuggestionServiceImpl suggestionService;

    @DisplayName("추천 팀 조회 테스트")
    @Nested
    class ReadSuggestion {

        @DisplayName("오늘의 추천 팀을 조회할 수 있다.")
        @Test
        void readSuggestion_User_Success() {
            Member member = MemberFixture.RIM.create_with_id(1L);
            LocalDateTime requestTime = LocalDateTime.now();

            Member teamLeader = MemberFixture.KAI.create();
            Team team = TeamFixture.WOMAN_TEAM.create(teamLeader, TeamMemberFixture.create_3_man());

            SuggestionTeamData data = SuggestionTeamData.builder()
                .teamMainImageUrl("testImageUrl")
                .teamLeader(TeamLeaderData.builder()
                    .nickname(teamLeader.getNickname())
                    .college(teamLeader.getCollegeInfo().getCollegeCode().getCodeValue())
                    .mbti(teamLeader.getMbti())
                    .profileImageUrl(teamLeader.getProfileImage().getBasicUrl())
                    .build())
                .team(team)
                .build();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(teamRepository.findHistory(1L, requestTime)).thenReturn(Collections.emptyList());
            when(teamRepository.findSuggestionTeamForUser(member.getMemberId(), member.getGender()))
                .thenReturn(List.of(data));

            List<SuggestionResponseDto> result = suggestionService.readSuggestion(
                member.getMemberId(), requestTime);

            verify(memberRepository).findById(1L);
            verify(teamRepository).findHistory(1L, requestTime);
            verify(teamRepository).findSuggestionTeamForUser(member.getMemberId(),
                member.getGender());

            assertThat(result.size()).isNotZero();
            assertThat(result.get(0)).extracting(
                "region", "memberNum", "profileImageURL", "mainImageURL").contains(
                team.getRegion().getName(),
                team.getMemberNum(),
                teamLeader.getProfileImage().getBasicUrl(),
                "testImageUrl");
        }

        @DisplayName("이미 오늘의 추천 팀을 조회한 경우 다시 추천을 받을 수는 없다.")
        @Test
        void readSuggestion_AlreadyGetSuggestion() {
            Member member = MemberFixture.RIM.create_with_id(1L);
            LocalDateTime requestTime = LocalDateTime.now();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(teamRepository.findHistory(1L, requestTime)).thenReturn(List.of(
                History.builder().build()));

            assertThatThrownBy(() -> {
                suggestionService.readSuggestion(1L, requestTime);
            }).isInstanceOf(SuggestionHistoryExistsException.class);

            verify(memberRepository).findById(1L);
            verify(teamRepository).findHistory(1L, requestTime);
        }
    }

    @DisplayName("추천 팀 조회 여부 확인 테스트")
    @Nested
    class CheckSuggestion {

        @DisplayName("오늘의 추천 팀 조회를 하지 않은 경우 조회 여부를 확인할 수 있다.")
        @Test
        void checkSuggestion_Success() {
            Member member = MemberFixture.RIM.create_with_id(1L);
            LocalDateTime requestTime = LocalDateTime.now();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(teamRepository.findHistory(1L, requestTime)).thenReturn(Collections.emptyList());

            CheckSuggestionResponseDto response = suggestionService.checkTodaySuggestionHistory(
                member.getMemberId(), requestTime);

            assertThat(response.isReceivedSuggestion()).isFalse();
            assertThat(response.teams()).isNull();
        }

        @DisplayName("오늘의 추천 팀을 이미 조회한 경우 해당 팀에 대한 정보를 함께 확인할 수 있다.")
        @Test
        void readSuggestion_Success() {
            Member member = MemberFixture.RIM.create_with_id(1L);
            LocalDateTime requestTime = LocalDateTime.now();

            Member member1 = MemberFixture.KAI.create_with_id(2L);
            Team suggestionTeam = TeamFixture.HONGDAE_TEAM_1.create(member1,
                TeamMemberFixture.create_3_man());

            History history = History.builder()
                .isLiked(false)
                .team(suggestionTeam)
                .member(member)
                .build();

            when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
            when(teamRepository.findHistory(1L, requestTime)).thenReturn(List.of(history));
            when(teamRepository.findSuggestionHistoryTeam(member.getMemberId(), requestTime))
                .thenReturn(List.of(
                    SuggestionHistoryData.builder()
                        .teamId(suggestionTeam.getTeamId())
                        .memberNum(suggestionTeam.getMemberNum())
                        .region(suggestionTeam.getRegion())
                        .teamMainImageUrl("testImageUrl")
                        .isLiked(false)
                        .teamLeader(TeamLeaderData.builder()
                            .nickname(member1.getNickname())
                            .college(member1.getCollegeInfo().getCollegeCode().getCodeValue())
                            .mbti(member1.getMbti())
                            .profileImageUrl(member1.getProfileImage().getBasicUrl())
                            .build())
                        .build()
                ));

            CheckSuggestionResponseDto response = suggestionService.checkTodaySuggestionHistory(
                member.getMemberId(), requestTime);

            assertThat(response.isReceivedSuggestion()).isTrue();
            assertThat(response.teams().size()).isNotZero();
        }
    }
}

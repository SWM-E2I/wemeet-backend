package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.dto.response.suggestion.TeamLeaderResponseDto;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MyTeamDetailResponseDto(
    int memberNum,
    String region,
    String drinkRate,
    String drinkWithGame,
    String additionalActivity,
    String introduction,
    String chatLink,
    String profileImageURL,
    List<TeamImageDto> images,
    List<TeamMemberResponseDto> members,
    TeamLeaderResponseDto leader
) {

    public static MyTeamDetailResponseDto of(Team team, List<TeamImageData> teamImages,
        Member teamLeader) {
        Optional<AdditionalActivity> additionalActivity = Optional.ofNullable(
            team.getAdditionalActivity());

        return MyTeamDetailResponseDto.builder()
            .memberNum(team.getMemberNum())
            .region(team.getRegion().getName())
            .drinkRate(team.getDrinkRate().getName())
            .drinkWithGame(team.getDrinkWithGame().getName())
            .additionalActivity(additionalActivity.map(AdditionalActivity::getName).orElse(null))
            .introduction(team.getIntroduction())
            .chatLink(team.getChatLink())
            .profileImageURL(teamLeader.getProfileImage().getLowUrl())
            .leader(TeamLeaderResponseDto.builder()
                .nickname(teamLeader.getNickname())
                .mbti(teamLeader.getMbti().name())
                .college(teamLeader.getCollegeInfo().getCollegeCode().getCodeValue())
                .admissionYear(teamLeader.getCollegeInfo().getAdmissionYear())
                .emailAuthenticated(teamLeader.getEmail() != null)
                .build()
            )
            .images(
                teamImages.stream()
                    .map(teamImage -> TeamImageDto.of(teamImage.url()))
                    .toList())
            .members(
                team.getTeamMembers().stream()
                    .map(TeamMemberResponseDto::of)
                    .toList())
            .build();
    }
}

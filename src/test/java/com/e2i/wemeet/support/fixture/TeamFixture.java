package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Getter;

@Getter
public enum TeamFixture {

    HONGDAE_TEAM_1(4, Region.HONGDAE, DrinkRate.LOW, DrinkWithGame.ANY,
        AdditionalActivity.CAFE, "홍대 팀 1"),
    ;

    private final Integer memberNum;
    private final Region region;
    private final DrinkRate drinkRate;
    private final DrinkWithGame drinkWithGame;
    private final AdditionalActivity additionalActivity;
    private final String introduction;

    TeamFixture(Integer memberNum, Region region, DrinkRate drinkRate, DrinkWithGame drinkWithGame,
        AdditionalActivity additionalActivity,
        String introduction) {
        this.memberNum = memberNum;
        this.region = region;
        this.drinkRate = drinkRate;
        this.drinkWithGame = drinkWithGame;
        this.additionalActivity = additionalActivity;
        this.introduction = introduction;
    }

    public Team create(Member teamLeader, List<TeamMember> teamMembers) {
        Team team = createBuilder(teamLeader)
            .build();
        team.addTeamMembers(teamMembers);
        return team;
    }

    public Team create_with_activity(Member teamLeader, List<TeamMember> teamMembers,
        AdditionalActivity additionalActivity) {
        Team team = createBuilder(teamLeader)
            .additionalActivity(additionalActivity)
            .build();
        team.addTeamMembers(teamMembers);
        return team;
    }

    // TODO :: refactor
    public CreateTeamRequestDto createTeamRequestDto() {
        return CreateTeamRequestDto.builder()
            .build();
    }

    // TODO :: refactor
    public UpdateTeamRequestDto modifyTeamRequestDto() {
        return UpdateTeamRequestDto.builder()
            .build();
    }

    // TODO :: refactor
    public MyTeamDetailResponseDto myTeamDetailResponseDto() {
        return MyTeamDetailResponseDto.builder()
            .build();
    }

    private Team.TeamBuilder createBuilder(final Member teamLeader) {
        return Team.builder()
            .teamLeader(teamLeader)
            .memberNum(this.memberNum)
            .region(this.region)
            .drinkRate(this.drinkRate)
            .drinkWithGame(this.drinkWithGame)
            .additionalActivity(this.additionalActivity)
            .introduction(this.introduction);
    }

    private void setTeamId(Team team, Long teamId) {
        Field field;
        try {
            field = team.getClass().getDeclaredField("teamId");
            field.setAccessible(true);
            field.set(team, teamId);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

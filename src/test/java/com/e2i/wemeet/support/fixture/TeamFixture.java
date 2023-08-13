package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.TeamMemberRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Getter;

@Getter
public enum TeamFixture {

    HONGDAE_TEAM_1(4, Region.HONGDAE, DrinkRate.LOW, DrinkWithGame.ANY,
        AdditionalActivity.CAFE, "안녕하세요! 반가워요! 홍대팀 1입니다!!"),
    WOMAN_TEAM(2, Region.SINCHON, DrinkRate.LOW, DrinkWithGame.ANY,
        AdditionalActivity.CAFE, "안녕하세요! 반가워요! 여자 2인 팀입니다!"),
    WOMAN_TEAM_2(2, Region.HONGDAE, DrinkRate.MIDDLE, DrinkWithGame.BEGINNER,
        AdditionalActivity.SHOW, "안녕하세요! 반가워요! 여자 2인 팀입니다!!!!!!!!!!");

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

    public CreateTeamRequestDto createTeamRequestDto_2_members() {
        TeamMemberRequestDto teamMember1 = TeamMemberFixture.OLIVER.createTeamMemberRequestDto();

        return createTeamRequestDto(List.of(teamMember1));
    }

    public CreateTeamRequestDto createTeamRequestDto_3_members() {
        TeamMemberRequestDto teamMember1 = TeamMemberFixture.OLIVER.createTeamMemberRequestDto();
        TeamMemberRequestDto teamMember2 = TeamMemberFixture.ELLI.createTeamMemberRequestDto();

        return createTeamRequestDto(List.of(teamMember1, teamMember2));
    }

    public CreateTeamRequestDto createTeamRequestDto(List<TeamMemberRequestDto> members) {
        return CreateTeamRequestDto.builder()
            .region(this.region.name())
            .drinkRate(this.drinkRate.name())
            .drinkWithGame(this.drinkWithGame.name())
            .additionalActivity(this.additionalActivity.name())
            .introduction(this.introduction)
            .members(members)
            .build();
    }

    public UpdateTeamRequestDto updateTeamRequestDto_2_members() {
        TeamMemberRequestDto teamMember1 = TeamMemberFixture.RACHEL.createTeamMemberRequestDto();

        return updateTeamRequestDto(List.of(teamMember1));
    }

    public UpdateTeamRequestDto updateTeamRequestDto_3_members() {
        TeamMemberRequestDto teamMember1 = TeamMemberFixture.OLIVER.createTeamMemberRequestDto();
        TeamMemberRequestDto teamMember2 = TeamMemberFixture.ELLI.createTeamMemberRequestDto();

        return updateTeamRequestDto(List.of(teamMember1, teamMember2));
    }

    public UpdateTeamRequestDto updateTeamRequestDto(List<TeamMemberRequestDto> members) {
        return UpdateTeamRequestDto.builder()
            .region(this.region.name())
            .drinkRate(this.drinkRate.name())
            .drinkWithGame(this.drinkWithGame.name())
            .additionalActivity(this.additionalActivity.name())
            .introduction(this.introduction)
            .members(members)
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

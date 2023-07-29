package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.AdditionalActivity;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Getter;

@Getter
public enum TeamFixture {

    TEST_TEAM(1L, "df42hg", 3, Gender.MALE, "건대입구", "0", AdditionalActivity.SHOW,
        "안녕하세요. 저희 팀은 멋쟁이 팀입니다.", MemberFixture.KAI.create()),
    HONGDAE_TEAM(null, "hongda", 3, null, "홍대 입구", "0", AdditionalActivity.SHOW,
        "안녕하세요. 홍대 팀 인사올립니다.", null);

    private final Long teamId;
    private final String teamCode;

    private final int memberCount;

    private final Gender gender;

    private final String region;

    private final String drinkingOption;

    private final AdditionalActivity additionalActivity;

    private final String introduction;

    private final Member member;

    TeamFixture(Long teamId, String teamCode, int memberCount, Gender gender, String region,
        String drinkingOption, AdditionalActivity additionalActivity, String introduction,
        Member member) {
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.memberCount = memberCount;
        this.gender = gender;
        this.region = region;
        this.drinkingOption = drinkingOption;
        this.additionalActivity = additionalActivity;
        this.introduction = introduction;
        this.member = member;
    }

    public Team create() {
        Team team = createBuilder()
            .build();
        setTeamId(team, this.teamId);
        return team;
    }

    public Team create(Member teamLeader) {
        return createTeamBuilder(teamLeader)
            .build();
    }

    public Team create_with_id(Member teamLeader, Long teamId) {
        Team team = createTeamBuilder(teamLeader)
            .build();
        setTeamId(team, teamId);
        return team;
    }

    public Team.TeamBuilder createTeamBuilder(Member member) {
        return Team.builder()
            .teamCode(member.getMemberId() + "@" + this.teamCode)
            .memberCount(this.memberCount)
            .region(this.region)
            .drinkingOption(this.drinkingOption)
            .additionalActivity(this.additionalActivity)
            .gender(member.getGender())
            .introduction(this.introduction)
            .teamLeader(member);
    }

    public CreateTeamRequestDto createTeamRequestDto() {
        return CreateTeamRequestDto.builder()
            .memberCount(this.memberCount)
            .region(this.region)
            .drinkingOption(this.drinkingOption)
            .additionalActivity(this.additionalActivity.toString())
            .preferenceMeetingTypeList(List.of("G001_C001"))
            .introduction(this.introduction)
            .build();
    }

    public ModifyTeamRequestDto modifyTeamRequestDto() {
        return ModifyTeamRequestDto.builder()
            .region("홍대 입구")
            .drinkingOption("1")
            .additionalActivity("CAFE")
            .preferenceMeetingTypeList(List.of("G001_C002"))
            .introduction("멋쟁이팀 인사드립니다.")
            .build();
    }

    public MyTeamDetailResponseDto myTeamDetailResponseDto() {
        return MyTeamDetailResponseDto.builder()
            .memberCount(this.memberCount)
            .region(this.region)
            .drinkingOption(this.drinkingOption)
            .additionalActivity(this.additionalActivity)
            .preferenceMeetingTypeList(List.of("G001_C002"))
            .introduction(this.introduction)
            .managerImageAuth(member.isImageAuth())
            .build();
    }

    private Team.TeamBuilder createBuilder() {
        return Team.builder()
            .teamCode(this.teamCode)
            .memberCount(this.memberCount)
            .gender(this.gender)
            .region(region)
            .drinkingOption(this.drinkingOption)
            .additionalActivity(this.additionalActivity)
            .teamLeader(this.member)
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

package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.exception.badrequest.TeamAlreadyExistsException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEAM")
@Entity
public class Team extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(length = 15, nullable = false)
    private String teamCode;

    @Column(nullable = false)
    private int memberCount;

    @Column(nullable = false)
    private boolean isActive;

    @Column(length = 6, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Column(length = 20, nullable = false)
    private String region;

    @Column(nullable = false)
    private String drinkingOption;

    @Column(length = 20)
    @Enumerated(value = EnumType.STRING)
    private AdditionalActivity additionalActivity;

    @Column(length = 100)
    private String introduction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member teamLeader;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    @Builder
    public Team(String teamCode, int memberCount, Gender gender,
        String drinkingOption, String region,
        AdditionalActivity additionalActivity,
        String introduction, Member teamLeader) {
        validateIsAbleManager(teamLeader);

        this.teamCode = teamCode;
        this.memberCount = memberCount;
        this.gender = gender;
        this.region = region;
        this.drinkingOption = drinkingOption;
        this.introduction = introduction;
        this.additionalActivity = additionalActivity;
        setTeamLeader(teamLeader);
    }

    public void updateTeam(ModifyTeamRequestDto modifyTeamRequestDto) {
        this.region = modifyTeamRequestDto.region();
        this.drinkingOption = modifyTeamRequestDto.drinkingOption();
        this.additionalActivity = AdditionalActivity.findBy(
            modifyTeamRequestDto.additionalActivity());
        this.introduction = modifyTeamRequestDto.introduction();
    }

    public void setTeamLeader(Member teamLeader) {
        this.teamLeader = teamLeader;
        this.members.add(teamLeader);
        teamLeader.setManager(this);
    }

    public void addMember(Member member) {
        if (!this.members.contains(member) && this.members.size() < this.memberCount) {
            this.members.add(member);
            member.setTeam(this);
        }
    }

    public void deleteMember(Member member) {
        if (this.members.contains(member)) {
            this.members.remove(member);
            member.setTeam(null);
        }
    }

    public void activateTeam() {
        if (!this.isActive && this.memberCount == this.members.size()) {
            setActive(true);
        }
    }

    public void deactivateTeam() {
        if (this.isActive && this.memberCount > this.members.size()) {
            setActive(false);
        }
    }

    private void setActive(boolean active) {
        isActive = active;
    }

    private void validateIsAbleManager(final Member manager) {
        isTeamExist(manager);
        isUnivAuth(manager);
    }

    private void isTeamExist(Member member) {
        if (member.getTeam() != null) {
            throw new TeamAlreadyExistsException();
        }
    }

    private void isUnivAuth(Member member) {
        if (member.getCollegeInfo().getMail() == null) {
            throw new UnAuthorizedUnivException();
        }
    }
}

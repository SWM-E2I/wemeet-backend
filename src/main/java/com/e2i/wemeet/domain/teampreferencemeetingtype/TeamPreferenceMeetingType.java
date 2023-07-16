package com.e2i.wemeet.domain.teampreferencemeetingtype;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TEAM_PREFERENCE_MEETING_TYPE")
@Entity
public class TeamPreferenceMeetingType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamPreferenceMeetingTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamId", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupCodeId", referencedColumnName = "groupCodeId")
    @JoinColumn(name = "codeId", referencedColumnName = "codeId")
    private Code code;

    @Builder
    public TeamPreferenceMeetingType(Long memberPreferenceMeetingTypeId, Team team,
        Code code) {
        this.teamPreferenceMeetingTypeId = memberPreferenceMeetingTypeId;
        this.team = team;
        this.code = code;
    }
}

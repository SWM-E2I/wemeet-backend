package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.util.validator.CustomFormatValidator.validateOpenChatLinkFormat;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Meeting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "partnerTeamId", referencedColumnName = "teamId", nullable = false)
    private Team partnerTeam;

    @Column(length = 50, nullable = false)
    private String chatLink;

    @Column(nullable = false)
    private Boolean isOver;

    @Builder
    public Meeting(Team team, Team partnerTeam, String chatLink) {
        this.team = team;
        this.partnerTeam = partnerTeam;
        this.chatLink = validateOpenChatLinkFormat(chatLink);
        this.isOver = false;
    }

    public void over() {
        this.isOver = true;
    }

}

package com.e2i.wemeet.domain.history;

import com.e2i.wemeet.domain.base.CreateTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "HISTORY")
@Entity
public class History extends CreateTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Boolean isLiked;

    @ManyToOne
    @JoinColumn(name = "teamId", referencedColumnName = "teamId", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "memberId", nullable = false)
    private Member member;

    @Builder
    public History(Boolean isLiked, Team team, Member member) {
        this.isLiked = isLiked;
        this.team = team;
        this.member = member;
    }

    public void updateIsLike(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}

package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @Column(length = 6, nullable = false)
    private String teamCode;

    @Column(nullable = false)
    private int memberCount;

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
    private Member member;

    @Builder
    public Team(Long teamId, String teamCode, int memberCount, Gender gender,
        String drinkingOption, String region,
        String introduction, Member member) {
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.memberCount = memberCount;
        this.gender = gender;
        this.region = region;
        this.drinkingOption = drinkingOption;
        this.introduction = introduction;
        this.member = member;
    }
}


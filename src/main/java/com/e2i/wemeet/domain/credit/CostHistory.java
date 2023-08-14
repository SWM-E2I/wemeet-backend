package com.e2i.wemeet.domain.credit;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CostHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long costHistoryId;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String costType;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private Integer costValue;

    public CostHistory(Member member, Earn earn, Integer costValue) {
        this.member = member;
        this.costType = Earn.getTypeName();
        this.detail = earn.name();
        this.costValue = costValue;
    }

    public CostHistory(Member member, Spent spent) {
        this.member = member;
        this.costType = Spent.getTypeName();
        this.detail = spent.name();
        this.costValue = spent.getValue();
    }

    public boolean isSpent() {
        return Spent.getTypeName().equals(this.costType);
    }

    public boolean isEarn() {
        return Earn.getTypeName().equals(this.costType);
    }
}

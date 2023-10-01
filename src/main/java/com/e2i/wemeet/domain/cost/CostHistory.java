package com.e2i.wemeet.domain.cost;

import com.e2i.wemeet.domain.base.CreateTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COST_HISTORY")
@Entity
public class CostHistory extends CreateTimeEntity {

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

    public CostHistory(Member member, Spent spent, Integer costValue) {
        this.member = member;
        this.costType = Spent.getTypeName();
        this.detail = spent.name();
        this.costValue = costValue;
    }

    public CostHistory(Member member, Payment payment, Integer costValue) {
        this.member = member;
        this.costType = Payment.getTypeName();
        this.detail = payment.name();
        this.costValue = costValue;
    }

    public boolean isSpent() {
        return Spent.getTypeName().equals(this.costType);
    }

    public boolean isEarn() {
        return Earn.getTypeName().equals(this.costType);
    }

    public boolean isPayment() {
        return Payment.getTypeName().equals(this.costType);
    }
}

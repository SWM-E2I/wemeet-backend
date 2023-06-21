package com.e2i.wemeet.domain.member_interest;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_INTEREST")
@Getter
@NoArgsConstructor
public class MemberInterest extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberInterestId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberId", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "groupCodeId", referencedColumnName = "groupCodeId")
  @JoinColumn(name = "CodeId", referencedColumnName = "CodeId")
  private Code code;

  @Builder
  public MemberInterest(Long memberInterestId, Member member, Code code) {
    this.memberInterestId = memberInterestId;
    this.member = member;
    this.code = code;
  }
}

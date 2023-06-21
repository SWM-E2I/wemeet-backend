package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(length = 4, nullable = false)
  private String memberCode;

  @Column(length = 20, nullable = false)
  private String nickname;

  @Column(length = 6, nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Gender gender;

  @Column(length = 30, nullable = false)
  private String college;

  @Column(length = 20, nullable = false)
  private String collegeType;

  @Column(nullable = false)
  private int admissionYear;

  @Column(length = 50, unique = true)
  private String mail;

  @Column(length = 13, unique = true, nullable = false)
  private String phoneNumber;

  @Column(length = 7, nullable = false)
  @Enumerated(value = EnumType.STRING)
  private Mbti mbti;

  @Column(length = 100)
  private String introduction;

  @Column
  private int startPreferenceAdmissionYear;

  @Column
  private int endPreferenceAdmissionYear;

  @Column(nullable = false)
  private int credit;

  @Column
  private boolean sameCollegeState;

  @Column
  private boolean drinkingOption;

  @Column
  private boolean isAvoidedFriends;

  @Embedded
  @Column
  private PreferenceMbti preferenceMbti;

  @Builder
  public Member(Long memberId, String memberCode, String nickname, Gender gender, String college,
      String collegeType, int admissionYear, String mail, String phoneNumber, Mbti mbti,
      String introduction, int startPreferenceAdmissionYear, int endPreferenceAdmissionYear,
      int credit, boolean sameCollegeState, boolean drinkingOption, boolean isAvoidedFriends,
      PreferenceMbti preferenceMbti) {
    this.memberId = memberId;
    this.memberCode = memberCode;
    this.nickname = nickname;
    this.gender = gender;
    this.college = college;
    this.collegeType = collegeType;
    this.admissionYear = admissionYear;
    this.mail = mail;
    this.phoneNumber = phoneNumber;
    this.mbti = mbti;
    this.introduction = introduction;
    this.startPreferenceAdmissionYear = startPreferenceAdmissionYear;
    this.endPreferenceAdmissionYear = endPreferenceAdmissionYear;
    this.credit = credit;
    this.sameCollegeState = sameCollegeState;
    this.drinkingOption = drinkingOption;
    this.isAvoidedFriends = isAvoidedFriends;
    this.preferenceMbti = preferenceMbti;
  }
}

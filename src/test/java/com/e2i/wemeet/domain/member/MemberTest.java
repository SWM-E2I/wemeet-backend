package com.e2i.wemeet.domain.member;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

  private final Member member = Member.builder()
      .memberCode("1234")
      .nickname("test_nickname")
      .gender(Gender.FEMALE)
      .college("서울대학교")
      .collegeType("자연과학대")
      .admissionYear(23)
      .mail("test@example.com")
      .phoneNumber("010-1234-1234")
      .mbti(Mbti.ENFJ)
      .introduction("test_introduction")
      .startPreferenceAdmissionYear(0)
      .endPreferenceAdmissionYear(0)
      .credit(10)
      .sameCollegeState(true)
      .drinkingOption(true)
      .isAvoidedFriends(false)
      .preferenceMbti(PreferenceMbti.builder()
          .eOrI(1)
          .sOrN(0)
          .tOrF(1)
          .jOrP(-1)
          .build())
      .build();

  @Test
  @DisplayName("개인 아이디 조회")
  void getMemberId() {
    assertNull(member.getMemberId());
  }

  @Test
  @DisplayName("개인 코드 조회")
  void getMemberCode() {
    assertNotNull(member.getMemberCode());
  }

  @Test
  @DisplayName("닉네임 조회")
  void getNickname() {
    assertNotNull(member.getNickname());
  }

  @Test
  @DisplayName("성별 조회")
  void getGender() {
    assertNotNull(member.getGender());
  }

  @Test
  @DisplayName("대학교 조회")
  void getCollege() {
    assertNotNull(member.getCollege());
  }

  @Test
  @DisplayName("단과대학 조회")
  void getCollegeType() {
    assertNotNull(member.getCollegeType());
  }

  @Test
  @DisplayName("학번 조회")
  void getAdmissionYear() {
    assertNotNull(member.getAdmissionYear());
  }

  @Test
  @DisplayName("메일 조회")
  void getMail() {
    assertNotNull(member.getMail());
  }

  @Test
  @DisplayName("휴대폰 번호 조회")
  void getPhoneNumber() {
    assertNotNull(member.getPhoneNumber());
  }

  @Test
  @DisplayName("Mbti 조회")
  void getMbti() {
    assertNotNull(member.getMbti());
  }

  @Test
  @DisplayName("자기소개 조회")
  void getIntroduction() {
    assertNotNull(member.getIntroduction());
  }

  @Test
  @DisplayName("선호 학번 시작점 조회")
  void getStartPreferenceAdmissionYear() {
    assertNotNull(member.getStartPreferenceAdmissionYear());
  }

  @Test
  @DisplayName("선호 학번 끝점 조회")
  void getEndPreferenceAdmissionYear() {
    assertNotNull(member.getEndPreferenceAdmissionYear());
  }

  @Test
  @DisplayName("크레딧 조회")
  void getCredit() {
    assertNotNull(member.getCredit());
  }

  @Test
  @DisplayName("같은 학교 매칭 여부 조회")
  void isSameCollegeState() {
    assertNotNull(member.isSameCollegeState());
  }

  @Test
  @DisplayName("음주 여부 조회")
  void isDrinkingOption() {
    assertNotNull(member.isDrinkingOption());
  }

  @Test
  @DisplayName("아는 사람 피하기 기능 여부 조회")
  void isAvoidedFriends() {
    assertNotNull(member.isAvoidedFriends());
  }
  
  @Test
  @DisplayName("선호 mbti 조회")
  void getPreferenceMbti() {
    assertNotNull(member.getPreferenceMbti());
  }
}

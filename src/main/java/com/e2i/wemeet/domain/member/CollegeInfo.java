package com.e2i.wemeet.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class CollegeInfo {

  @Column(length = 30, nullable = false)
  private String college;

  @Column(length = 20, nullable = false)
  private String collegeType;

  @Column(nullable = false)
  private int admissionYear;

  @Column(length = 50, unique = true)
  private String mail;

  @Builder
  public CollegeInfo(String college, String collegeType, int admissionYear, String mail) {
    this.college = college;
    this.collegeType = collegeType;
    this.admissionYear = admissionYear;
    this.mail = mail;
  }
}

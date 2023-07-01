package com.e2i.wemeet.controller.admin;

import com.e2i.wemeet.domain.member.CollegeInfo;

public enum AdminCollegeInfoFixture {
    ANYANG(2017, "공대", "안양대학교", "pppp1234@anyang.ac.kr"),
    SEOUL_WOMAN(2018, "공대", "서울여자대학교", "pppp1234@seoul.ac.kr"),
    KU(2022, "인문", "고려대학교", "pppp1234@korea.ac.kr"),
    ;

    private final int admissionYear;
    private final String collegeType;
    private final String college;
    private final String mail;

    AdminCollegeInfoFixture(int admissionYear, String collegeType, String college, String mail) {
        this.admissionYear = admissionYear;
        this.collegeType = collegeType;
        this.college = college;
        this.mail = mail;
    }

    public CollegeInfo create() {
        return new CollegeInfo(college, collegeType, admissionYear, mail);
    }

    public int getAdmissionYear() {
        return admissionYear;
    }

    public String getCollegeType() {
        return collegeType;
    }

    public String getCollege() {
        return college;
    }

    public String getMail() {
        return mail;
    }
}

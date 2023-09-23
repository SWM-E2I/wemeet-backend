package com.e2i.wemeet.controller.admin;

import com.e2i.wemeet.domain.member.data.CollegeInfo;

// TODO :: service refactoring
public enum AdminCollegeInfoFixture {
    ANYANG("17", "공대", "안양대학교", "pppp1234@anyang.ac.kr"),
    SEOUL_WOMAN("18", "공대", "서울여자대학교", "pppp1234@seoul.ac.kr"),
    KU("22", "인문", "고려대학교", "pppp1234@korea.ac.kr"),
    ;

    private final String admissionYear;
    private final String collegeType;
    private final String college;
    private final String mail;

    AdminCollegeInfoFixture(String admissionYear, String collegeType, String college, String mail) {
        this.admissionYear = admissionYear;
        this.collegeType = collegeType;
        this.college = college;
        this.mail = mail;
    }

    public CollegeInfo create() {
        return null;
    }

    public String getAdmissionYear() {
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

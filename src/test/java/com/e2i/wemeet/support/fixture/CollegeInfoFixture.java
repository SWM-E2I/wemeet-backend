package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.CollegeInfo;

public enum CollegeInfoFixture {
    ANYANG_COLLEGE(2022, "공대", "안양대학교", "pppp1234@anyang.ac.kr")
    ;

    private final int admissionYear;
    private final String collegeType;
    private final String college;
    private final String mail;

    CollegeInfoFixture(int admissionYear, String collegeType, String college, String mail) {
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

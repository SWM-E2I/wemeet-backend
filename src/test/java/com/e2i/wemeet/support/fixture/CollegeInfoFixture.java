package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.dto.request.member.CollegeInfoRequestDto;

public enum CollegeInfoFixture {
    ANYANG_COLLEGE("22", "공대", "안양대학교", "pppp1234@anyang.ac.kr"),
    KOREA_COLLEGE("18", "공대", "고려대학교", "afgds234@korea.ac.kr"),
    SEOULWOMEN_COLLEGE("19", "공대", "서울여자대학교", "fgafd1234@swu.ac.kr");

    private final String admissionYear;
    private final String collegeType;
    private final String college;
    private final String mail;

    CollegeInfoFixture(String admissionYear, String collegeType, String college, String mail) {
        this.admissionYear = admissionYear;
        this.collegeType = collegeType;
        this.college = college;
        this.mail = mail;
    }

    public CollegeInfo create() {
        return new CollegeInfo(college, collegeType, admissionYear, mail);
    }

    public CollegeInfoRequestDto createCollegeInfoDto() {
        return CollegeInfoRequestDto.builder()
            .admissionYear(this.admissionYear)
            .college(this.college)
            .collegeType(this.collegeType)
            .build();
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

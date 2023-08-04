package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.support.fixture.code.CodeFixture;
import lombok.Getter;

@Getter
public enum CollegeInfoFixture {
    BASIC(null, CollegeType.ENGINEERING, "20"),
    KOREA(CodeFixture.KOREA_UNIVERSITY.create(), CollegeType.ENGINEERING, "18"),
    ANYANG(CodeFixture.ANYANG_UNIVERSITY.create(), CollegeType.SOCIAL, "17"),
    WOMAN(CodeFixture.WOMANS_UNIVERSITY.create(), CollegeType.ARTS, "23"),
    INHA(CodeFixture.INHA_UNIVERSITY.create(), CollegeType.ENGINEERING, "22"),
    ;

    private final Code collegeCode;
    private final CollegeType collegeType;
    private final String admissionYear;

    CollegeInfoFixture(Code collegeCode, CollegeType collegeType, String admissionYear) {
        this.collegeCode = collegeCode;
        this.collegeType = collegeType;
        this.admissionYear = admissionYear;
    }

    public CollegeInfo create() {
        return createBuilder()
            .build();
    }

    public CollegeInfo create(Code collegeCode) {
        return createBuilder()
            .collegeCode(collegeCode)
            .build();
    }

    private CollegeInfo.CollegeInfoBuilder createBuilder() {
        return CollegeInfo.builder()
            .collegeCode(this.collegeCode)
            .collegeType(this.collegeType)
            .admissionYear(this.admissionYear);
    }
}

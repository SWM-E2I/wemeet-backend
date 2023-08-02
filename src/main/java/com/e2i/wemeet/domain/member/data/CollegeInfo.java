package com.e2i.wemeet.domain.member.data;

import com.e2i.wemeet.domain.code.Code;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CollegeInfo {

    @ManyToOne
    @JoinColumn(name = "collegeCodeId", referencedColumnName = "codeId")
    @JoinColumn(name = "collegeGroupCodeId", referencedColumnName = "groupCodeId")
    private Code collegeCode;

    @Column(length = 20, nullable = false)
    private String collegeType;

    @Column(length = 2, nullable = false)
    private String admissionYear;

    @Builder
    public CollegeInfo(Code collegeCode, String collegeType, String admissionYear) {
        this.collegeCode = collegeCode;
        this.collegeType = collegeType;
        this.admissionYear = admissionYear;
    }
}

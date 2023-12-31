package com.e2i.wemeet.domain.member.data;

import com.e2i.wemeet.domain.base.converter.CollegeTypeConverter;
import com.e2i.wemeet.domain.code.Code;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collegeCodeId", referencedColumnName = "code_id")
    @JoinColumn(name = "collegeGroupCodeId", referencedColumnName = "group_code_id")
    private Code collegeCode;

    @Convert(converter = CollegeTypeConverter.class)
    private CollegeType collegeType;

    @Column(length = 2, nullable = false)
    private String admissionYear;

    @Builder
    public CollegeInfo(Code collegeCode, CollegeType collegeType, String admissionYear) {
        this.collegeCode = collegeCode;
        this.collegeType = collegeType;
        this.admissionYear = admissionYear;
    }
}

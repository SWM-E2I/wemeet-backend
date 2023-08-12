package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.util.validator.bean.CollegeCodeValid;
import com.e2i.wemeet.util.validator.bean.CollegeTypeValid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CollegeInfoRequestDto(

    @NotNull
    @CollegeCodeValid
    String collegeCode,

    @NotNull
    @CollegeTypeValid
    String collegeType,

    @NotNull
    @Length(message = "{length.admissionYear}", min = 2, max = 2)
    String admissionYear

) {

    public CollegeInfo toCollegeInfo(Code collegeCode) {
        return CollegeInfo.builder()
            .collegeCode(collegeCode)
            .collegeType(CollegeType.valueOf(this.collegeType))
            .admissionYear(admissionYear)
            .build();
    }
}

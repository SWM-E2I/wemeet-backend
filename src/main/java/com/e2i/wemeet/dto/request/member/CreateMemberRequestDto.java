package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.util.validator.bean.GenderValid;
import com.e2i.wemeet.util.validator.bean.MbtiValid;
import com.e2i.wemeet.util.validator.bean.NicknameValid;
import com.e2i.wemeet.util.validator.bean.PhoneValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMemberRequestDto(

    @NotNull
    @NicknameValid
    String nickname,

    @NotNull
    @GenderValid
    String gender,

    @NotNull
    @PhoneValid
    String phoneNumber,

    @Valid
    CollegeInfoRequestDto collegeInfo,

    @NotNull
    @MbtiValid
    String mbti

) {

    public Member toEntity(Code collegeCode) {
        return Member.builder()
            .nickname(this.nickname.trim())
            .gender(Gender.valueOf(this.gender))
            .phoneNumber(this.phoneNumber)
            .collegeInfo(this.collegeInfo.toCollegeInfo(collegeCode))
            .mbti(Mbti.valueOf(this.mbti))
            .credit(10)
            .imageAuth(false)
            .role(Role.USER)
            .build();
    }
}

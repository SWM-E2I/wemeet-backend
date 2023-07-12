package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.ANYANG_COLLEGE;
import static com.e2i.wemeet.support.fixture.PreferenceFixture.GENERAL_PREFERENCE;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import java.util.List;

public enum MemberFixture {
    KAI("4100", "kai", Gender.MALE, "+821012341234",
        ANYANG_COLLEGE.create(), GENERAL_PREFERENCE.create(),
        Mbti.INFJ, "안녕하세요", 100, Role.USER);

    private final String memberCode;
    private final String nickname;
    private final Gender gender;
    private final String phoneNumber;
    private final CollegeInfo collegeInfo;
    private final Preference preference;
    private final Mbti mbti;
    private final String introduction;
    private final int credit;
    private final Role role;

    MemberFixture(String memberCode, String nickname, Gender gender, String phoneNumber,
        CollegeInfo collegeInfo, Preference preference, Mbti mbti, String introduction, int credit,
        Role role) {
        this.memberCode = memberCode;
        this.nickname = nickname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.collegeInfo = collegeInfo;
        this.preference = preference;
        this.mbti = mbti;
        this.introduction = introduction;
        this.credit = credit;
        this.role = role;
    }

    public Member create() {
        return createBuilder()
            .build();
    }

    public Member create_credit(final int credit) {
        return createBuilder()
            .credit(credit)
            .build();
    }

    public Member create_role(final Role role) {
        return createBuilder()
            .role(role)
            .build();
    }

    public Member create_role_credit(final Role role, final int credit) {
        return createBuilder()
            .role(role)
            .credit(credit)
            .build();
    }

    private Member.MemberBuilder createBuilder() {
        return Member.builder()
            .memberCode(this.memberCode)
            .nickname(this.nickname)
            .gender(this.gender)
            .phoneNumber(this.phoneNumber)
            .collegeInfo(this.collegeInfo)
            .preference(this.preference)
            .mbti(this.mbti)
            .introduction(this.introduction)
            .credit(this.credit)
            .role(this.role);
    }

    public CreateMemberRequestDto createMemberRequestDto() {
        return CreateMemberRequestDto.builder()
            .nickname(this.nickname)
            .gender(this.gender.toString())
            .phoneNumber(this.phoneNumber)
            .collegeInfo(ANYANG_COLLEGE.createCollegeInfoDto())
            .preference(GENERAL_PREFERENCE.createPreferenceDto())
            .preferenceMeetingTypeList(List.of())
            .memberInterestList(List.of())
            .mbti("ESTJ")
            .introduction("hello!!").build();
    }

    public MemberDetailResponseDto createMemberDetailResponseDto() {
        return MemberDetailResponseDto.builder()
            .nickname(this.nickname)
            .gender(this.gender)
            .mbti(this.mbti)
            .college(this.collegeInfo.getCollege())
            .collegeType(this.collegeInfo.getCollegeType())
            .admissionYear(this.collegeInfo.getAdmissionYear())
            .introduction(this.introduction)
            .profileImageList(List.of())
            .memberInterestList(List.of())
            .build();
    }

    public ModifyMemberRequestDto createModifyMemberRequestDto() {
        return ModifyMemberRequestDto.builder()
            .nickname("modify nickname")
            .introduction("modify introduction")
            .mbti("ESTJ")
            .memberInterestList(List.of())
            .build();
    }

    public MemberInfoResponseDto createMemberInfoResponseDto() {
        return MemberInfoResponseDto.builder()
            .nickname(this.nickname)
            .memberCode(this.memberCode)
            .profileImage("profileImage Key")
            .univAuth(true)
            .imageAuth(false)
            .build();
    }

    public String getMemberCode() {
        return memberCode;
    }

    public String getNickname() {
        return nickname;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public CollegeInfo getCollegeInfo() {
        return collegeInfo;
    }

    public Mbti getMbti() {
        return mbti;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getCredit() {
        return credit;
    }

    public Role getRole() {
        return role;
    }
}

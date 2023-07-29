package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.ANYANG_COLLEGE;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.KOREA_COLLEGE;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.SEOULWOMEN_COLLEGE;
import static com.e2i.wemeet.support.fixture.PreferenceFixture.GENERAL_PREFERENCE;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import java.lang.reflect.Field;
import java.util.List;

public enum MemberFixture {
    KAI(1L, "4100", "kai", Gender.MALE, "+821012341234",
        ANYANG_COLLEGE.create(), GENERAL_PREFERENCE.create(),
        Mbti.INFJ, "안녕하세요", 100, Role.USER, false),

    RIM(2L, "4101", "rim", Gender.FEMALE, "+821056785678",
        KOREA_COLLEGE.create(), GENERAL_PREFERENCE.create(),
        Mbti.INFJ, "안녕하세요", 100, Role.USER, false),

    SEYUN(3L, "4102", "seyun", Gender.MALE, "+821056785628",
        SEOULWOMEN_COLLEGE.create(), GENERAL_PREFERENCE.create(),
        Mbti.INFJ, "안녕하세요", 100, Role.MANAGER, true),

    JEONGYEOL(4L, "4103", "10cm", Gender.MALE, "+821056783678",
        KOREA_COLLEGE.create(), GENERAL_PREFERENCE.create(),
        Mbti.INFJ, "안녕하세요", 100, Role.USER, true);

    private final Long memberId;
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

    private final boolean imageAuth;

    MemberFixture(Long memberId, String memberCode, String nickname, Gender gender,
        String phoneNumber,
        CollegeInfo collegeInfo, Preference preference, Mbti mbti, String introduction, int credit,
        Role role, boolean imageAuth) {
        this.memberId = memberId;
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
        this.imageAuth = imageAuth;
    }

    public Member create() {
        Member member = createBuilder()
            .build();
        setMemberId(this.memberId, member);
        return member;
    }

    public Member create_with_id(final Long memberId) {
        Member member = createBuilder()
            .build();
        setMemberId(memberId, member);
        return member;
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

    public Member create_preference(final Preference preference) {
        return createBuilder()
            .preference(preference)
            .build();
    }

    public Member create_college(final CollegeInfo collegeInfo) {
        return createBuilder()
            .collegeInfo(collegeInfo)
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
            .role(this.role)
            .imageAuth(this.imageAuth);
    }

    public CreateMemberRequestDto createMemberRequestDto() {
        return CreateMemberRequestDto.builder()
            .nickname(this.nickname)
            .gender(this.gender.toString())
            .phoneNumber(this.phoneNumber)
            .collegeInfo(ANYANG_COLLEGE.createCollegeInfoDto())
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
            .build();
    }

    public ModifyMemberRequestDto createModifyMemberRequestDto() {
        return ModifyMemberRequestDto.builder()
            .nickname("modify nickname")
            .introduction("modify introduction")
            .mbti("ESTJ")
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

    public InviteTeamRequestDto inviteTeamRequestDto() {
        return InviteTeamRequestDto.builder()
            .nickname(this.nickname)
            .memberCode(this.memberCode)
            .build();
    }

    public Long getMemberId() {
        return memberId;
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

    private void setMemberId(Long memberId, Member member) {
        Field memberIdField;
        try {
            memberIdField = member.getClass().getDeclaredField("memberId");
            memberIdField.setAccessible(true);
            memberIdField.set(member, memberId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

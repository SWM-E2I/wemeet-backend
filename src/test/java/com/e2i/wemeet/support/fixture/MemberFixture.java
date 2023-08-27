package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.ANYANG;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.KOREA;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.WOMAN;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.dto.request.member.CollegeInfoRequestDto;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import java.lang.reflect.Field;

public enum MemberFixture {
    KAI("카이", Gender.MAN, "+821011112222", "2017e7024@gs.anyang.ac.kr",
        ANYANG.create(), Mbti.INFJ, 100, false,
        "/v1/asdf", "/v1/kai", Role.USER),
    RIM("째림", Gender.WOMAN, "+821098764444", "2019a24@woman.ac.kr",
        WOMAN.create(), Mbti.ISFJ, 100, false,
        "/v1/asdf", "/v1/rim", Role.USER),
    SEYUN("떼윤", Gender.MAN, "+821090908888", "2020a234@korea.ac.kr",
        KOREA.create(), Mbti.ENFJ, 100, false,
        "/v1/asdf", "/v1/sey", Role.USER),
    JEONGYEOL("정열", Gender.MAN, "+8210333344444", "2014p13@korea.ac.kr",
        KOREA.create(), Mbti.ESFJ, 100, false,
        "/v1/asdf", "/v1/jeong", Role.USER),

    CHAEWON("채원", Gender.WOMAN, "+821089071365", "2020p13@woman.ac.kr",
        WOMAN.create(), Mbti.ISTJ, 100, false,
        "/v1/asdf", "/v1/chaechae", Role.USER);

    private final String nickname;
    private final Gender gender;
    private final String phoneNumber;
    private final String email;
    private final CollegeInfo collegeInfo;
    private final Mbti mbti;
    private final Integer credit;
    private final Boolean imageAuth;
    private final String basicUrl;
    private final String lowUrl;
    private final Role role;

    MemberFixture(String nickname, Gender gender, String phoneNumber, String email,
        CollegeInfo collegeInfo, Mbti mbti, Integer credit,
        Boolean imageAuth, String basicUrl, String lowUrl, Role role) {
        this.nickname = nickname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
        this.credit = credit;
        this.imageAuth = imageAuth;
        this.basicUrl = basicUrl;
        this.lowUrl = lowUrl;
        this.role = role;
    }

    public Member create() {
        return createBuilder()
            .build();
    }

    // 대학 코드를 입력하여 생성
    public Member create(Code collegeCode) {
        CollegeInfo collegeInfoFixture = CollegeInfoFixture.ENGINERRING.create(collegeCode);

        return createBuilder()
            .collegeInfo(collegeInfoFixture)
            .build();
    }

    public Member create_phone(final String phoneNumber) {
        return createBuilder()
            .phoneNumber(phoneNumber)
            .build();
    }

    public Member create_email(final String email) {
        return createBuilder()
            .email(email)
            .build();
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

    public Member create_credit(Code collegeCode, final int credit) {
        CollegeInfo collegeInfoFixture = CollegeInfoFixture.ENGINERRING.create(collegeCode);

        return createBuilder()
            .credit(credit)
            .collegeInfo(collegeInfoFixture)
            .build();
    }

    public Member create_profile_image(final ProfileImage profileImage) {
        return createBuilder()
            .profileImage(profileImage)
            .build();
    }

    public Member create_image_auth(final boolean imageAuth) {
        return createBuilder()
            .profileImage(new ProfileImage(this.basicUrl, this.lowUrl, imageAuth))
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

    public Member create_college(final CollegeInfo collegeInfo) {
        return createBuilder()
            .collegeInfo(collegeInfo)
            .build();
    }

    private Member.MemberBuilder createBuilder() {
        return Member.builder()
            .nickname(this.nickname)
            .gender(this.gender)
            .phoneNumber(this.phoneNumber)
            .email(this.email)
            .collegeInfo(this.collegeInfo)
            .mbti(this.mbti)
            .credit(this.credit)
            .profileImage(new ProfileImage(this.basicUrl, this.lowUrl, this.imageAuth))
            .role(this.role);
    }

    public CreateMemberRequestDto createMemberRequestDto() {
        CollegeInfoRequestDto collegeInfoRequestDto = createCollegeInfoRequestDto();

        return CreateMemberRequestDto.builder()
            .nickname(this.nickname)
            .phoneNumber(this.phoneNumber)
            .gender(this.gender.name())
            .mbti(this.mbti.name())
            .collegeInfo(collegeInfoRequestDto)
            .build();
    }

    public CreateMemberRequestDto createMemberRequestDto(String collegeCode) {
        CollegeInfoRequestDto collegeInfoRequestDto = createCollegeInfoRequestDto(collegeCode);

        return CreateMemberRequestDto.builder()
            .nickname(this.nickname)
            .phoneNumber(this.phoneNumber)
            .gender(this.gender.name())
            .mbti(this.mbti.name())
            .collegeInfo(collegeInfoRequestDto)
            .build();
    }

    public MemberDetailResponseDto createMemberDetailResponseDto() {
        return MemberDetailResponseDto.of(
            this.create(),
            this.collegeInfo.getCollegeCode().getCodeValue()
        );
    }

    public UpdateMemberRequestDto createUpdateMemberRequestDto(final String nickname,
        final String mbti) {
        String updateNickname = nickname == null ? this.nickname : nickname;
        String updateMbti = mbti == null ? this.mbti.name() : mbti;

        return UpdateMemberRequestDto.builder()
            .nickname(updateNickname)
            .mbti(updateMbti)
            .build();
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

    public String getEmail() {
        return email;
    }

    public CollegeInfo getCollegeInfo() {
        return collegeInfo;
    }

    public Mbti getMbti() {
        return mbti;
    }

    public Integer getCredit() {
        return credit;
    }

    public Boolean getImageAuth() {
        return imageAuth;
    }

    public String getBasicUrl() {
        return basicUrl;
    }

    public String getLowUrl() {
        return lowUrl;
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

    private CollegeInfoRequestDto createCollegeInfoRequestDto() {
        return CollegeInfoRequestDto.builder()
            .collegeCode(getCollegeCodeWithDelimiter(this.collegeInfo.getCollegeCode()))
            .collegeType(this.collegeInfo.getCollegeType().name())
            .admissionYear(this.collegeInfo.getAdmissionYear())
            .build();
    }

    private CollegeInfoRequestDto createCollegeInfoRequestDto(String collegeCode) {
        return CollegeInfoRequestDto.builder()
            .collegeCode(collegeCode)
            .collegeType(this.collegeInfo.getCollegeType().name())
            .admissionYear(this.collegeInfo.getAdmissionYear())
            .build();
    }

    // Delimiter가 붙은 코드를 생성 ex) CE-001
    private String getCollegeCodeWithDelimiter(Code code) {
        CodePk codePk = code.getCodePk();
        return codePk.getGroupCodeId() + "-" + codePk.getCodeId();
    }
}

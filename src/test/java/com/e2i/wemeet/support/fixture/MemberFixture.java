package com.e2i.wemeet.support.fixture;

import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.ANYANG;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.KOREA;
import static com.e2i.wemeet.support.fixture.CollegeInfoFixture.WOMAN;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import java.lang.reflect.Field;
import lombok.Getter;

// TODO :: service refactoring
@Getter
public enum MemberFixture {
    KAI("kai", Gender.MAN, "+821056785678", "2017e7024@as.ac.kr",
        ANYANG.create(), Mbti.INFJ, 100, false,
        "/v1/asdf", "/v1/idwq", Role.USER),
    RIM("rim", Gender.WOMAN, "+821056785678", "2019a24@gad.ac.kr",
        WOMAN.create(), Mbti.ISFJ, 100, false,
        "/v1/asdf", "/v1/idwq", Role.USER),
    SEYUN("seyun", Gender.MAN, "+821056785678", "2020a234@ad.ac.kr",
        KOREA.create(), Mbti.ENFJ, 100, false,
        "/v1/asdf", "/v1/idwq", Role.USER),
    JEONGYEOL("jeongyeol", Gender.MAN, "+821056785678", "2014p13@pe.ac.kr",
        KOREA.create(), Mbti.ESFJ, 100, false,
        "/v1/asdf", "/v1/idwq", Role.USER);

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

    MemberFixture(String nickname, Gender gender, String phoneNumber, String email, CollegeInfo collegeInfo, Mbti mbti, Integer credit,
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
        CollegeInfo collegeInfoFixture = CollegeInfoFixture.BASIC.create(collegeCode);

        return createBuilder()
            .collegeInfo(collegeInfoFixture)
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
            .imageAuth(this.imageAuth)
            .profileImage(new ProfileImage(this.basicUrl, this.lowUrl))
            .role(this.role);
    }

    public CreateMemberRequestDto createMemberRequestDto() {
        return CreateMemberRequestDto.builder()
            .nickname(this.nickname)
            .gender(this.gender.toString())
            .phoneNumber(this.phoneNumber)
            .mbti("ESTJ")
            .introduction("hello!!").build();
    }

    // TODO :: service refactoring
    public MemberDetailResponseDto createMemberDetailResponseDto() {
        return MemberDetailResponseDto.builder()
            .nickname(this.nickname)
            .gender(this.gender)
            .mbti(this.mbti)
            .admissionYear(this.collegeInfo.getAdmissionYear())
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
            .profileImage("profileImage Key")
            .univAuth(true)
            .imageAuth(false)
            .build();
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

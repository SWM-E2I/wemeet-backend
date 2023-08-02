package com.e2i.wemeet.controller.admin;

import static com.e2i.wemeet.controller.admin.AdminCollegeInfoFixture.ANYANG;
import static com.e2i.wemeet.controller.admin.AdminCollegeInfoFixture.KU;
import static com.e2i.wemeet.controller.admin.AdminCollegeInfoFixture.SEOUL_WOMAN;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.Role;
import java.util.Arrays;

// TODO :: service refactoring
public enum AdminMemberFixture {
    KAI("4100", "kai", Gender.MAN, "+821012341234",
        ANYANG.create(), Mbti.INFJ, "hi", 100, Role.USER),
    RIM("4101", "rim", Gender.WOMAN, "+821088990011",
        SEOUL_WOMAN.create(), Mbti.ISTP, "hello", 100, Role.MANAGER),
    SEYUN("4102", "seyun", Gender.MAN, "+821033445566",
        KU.create(), Mbti.ESFJ, "hey", 100, Role.USER);

    private final String memberCode;
    private final String nickname;
    private final Gender gender;
    private final String phoneNumber;
    private final CollegeInfo collegeInfo;
    private final Mbti mbti;
    private final String introduction;
    private final int credit;
    private final Role role;

    AdminMemberFixture(String memberCode, String nickname, Gender gender, String phoneNumber,
        CollegeInfo collegeInfo, Mbti mbti, String introduction, int credit,
        Role role) {
        this.memberCode = memberCode;
        this.nickname = nickname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
        this.introduction = introduction;
        this.credit = credit;
        this.role = role;
    }

    public Member create() {
        return createBuilder()
            .build();
    }

    public Member createWithCredit(final int credit) {
        return createBuilder()
            .credit(credit)
            .build();
    }

    public Member createWithRole(final Role role) {
        return createBuilder()
            .role(role)
            .build();
    }

    public Member createWithRoleCredit(final Role role, final int credit) {
        return createBuilder()
            .role(role)
            .credit(credit)
            .build();
    }

    private Member.MemberBuilder createBuilder() {
        return Member.builder()
            .nickname(this.nickname)
            .gender(this.gender)
            .phoneNumber(this.phoneNumber)
            .collegeInfo(this.collegeInfo)
            .mbti(this.mbti)
            .credit(this.credit)
            .role(this.role);
    }

    public static AdminMemberFixture getFixture(final String fixture) {
        return Arrays.stream(AdminMemberFixture.values())
            .filter(memberFixture -> memberFixture.name().equals(fixture))
            .findFirst().orElseThrow();
    }
}

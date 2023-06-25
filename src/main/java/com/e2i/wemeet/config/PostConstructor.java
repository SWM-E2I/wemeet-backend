package com.e2i.wemeet.config;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostConstructor {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void initMember() {
        CollegeInfo c = CollegeInfo.builder()
            .admissionYear(2022)
            .collegeType("공대")
            .college("경희대학교")
            .mail("ddfsf@kys.ac.kr")
            .build();

        Member member = Member.builder()
            .memberCode("1234")
            .nickname("nick")
            .gender(Gender.FEMALE)
            .phoneNumber("01083265588")
            .collegeInfo(c)
            .mbti(Mbti.ENFJ)
            .introduction("Hi")
            .credit(0)
            .role(Role.USER)
            .build();

        memberRepository.save(member);
    }
}

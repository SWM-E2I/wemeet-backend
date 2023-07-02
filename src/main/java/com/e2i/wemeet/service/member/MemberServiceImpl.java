package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.memberinterest.MemberInterestRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.exception.badrequest.DuplicatedMailException;
import com.e2i.wemeet.exception.badrequest.DuplicatedPhoneNumberException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;

    @Override
    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
    }

    @Override
    @Transactional
    public Member createMember(CreateMemberRequestDto requestDto) {
        memberRepository.findByPhoneNumber(requestDto.phoneNumber())
            .ifPresent(member -> {
                throw new DuplicatedPhoneNumberException();
            });

        memberRepository.findByCollegeInfoMail(requestDto.collegeInfo().mail())
            .ifPresent(member -> {
                throw new DuplicatedMailException();
            });

        String memberCode = createMemberCode();
        return memberRepository.save(requestDto.toEntity(memberCode));
    }

    @Override
    @Transactional
    public void modifyMember(Long memberId, ModifyMemberRequestDto requestDto,
        List<Code> modifyCode) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);

        member.modifyNickname(requestDto.nickname());
        member.modifyIntroduction(requestDto.introduction());
        member.modifyMbti(requestDto.mbti());

        List<MemberInterest> memberInterests = modifyCode.stream()
            .map(memberInterestCode -> MemberInterest.builder()
                .member(member)
                .code(memberInterestCode)
                .build())
            .toList();

        memberInterestRepository.deleteAllByMemberMemberId(member.getMemberId());
        memberInterestRepository.saveAll(memberInterests);
    }

    private String createMemberCode() {
        int code = new Random().nextInt(9000) + 1000;
        return String.valueOf(code);
    }
}

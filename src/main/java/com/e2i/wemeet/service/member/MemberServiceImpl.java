package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.exception.badrequest.DuplicatedMailException;
import com.e2i.wemeet.exception.badrequest.DuplicatedPhoneNumberException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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

    private String createMemberCode() {
        int code = new Random().nextInt(9000) + 1000;
        return String.valueOf(code);
    }
}

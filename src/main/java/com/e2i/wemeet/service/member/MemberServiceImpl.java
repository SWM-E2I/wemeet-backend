package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // TODO :: service refactoring
    @Override
    @Transactional
    public Long createMember(CreateMemberRequestDto requestDto) {

        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional
    public void modifyMember(Long memberId, ModifyMemberRequestDto requestDto) {

    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public MemberDetailResponseDto getMemberDetail(Long memberId) {
        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getMemberRole(Long memberId) {
        return null;
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        // member.delete();

    }
}

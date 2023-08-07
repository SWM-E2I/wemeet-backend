package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CodeRepository codeRepository;

    @Transactional
    @Override
    public Long createMember(final CreateMemberRequestDto requestDto) {
        Code code = getCode(requestDto.collegeInfo().collegeCode());

        Member member = memberRepository.save(requestDto.toEntity(code));
        return member.getMemberId();
    }

    @Transactional(readOnly = true)
    @Override
    public MemberDetailResponseDto readMemberDetail(final Long memberId) {
        Member member = memberRepository.findByIdFetchCode(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        String collegeName = member.getCollegeInfo()
            .getCollegeCode()
            .getCodeValue();

        return MemberDetailResponseDto.of(member, collegeName);
    }

    @Override
    public MemberRoleResponseDto readMemberRole(final MemberPrincipal memberPrincipal) {
        return MemberRoleResponseDto.of(memberPrincipal);
    }

    @Transactional
    @Override
    public void updateMember(Long memberId, UpdateMemberRequestDto requestDto) {

    }

    @Transactional
    @Override
    public void deleteMember(Long memberId, LocalDateTime deletedAt) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        member.delete(deletedAt);
    }

    private Code getCode(final String groupCodeIdWithCodeId) {
        CodePk collegeCodePk = CodePk.of(groupCodeIdWithCodeId);
        return codeRepository.findByCodePk(collegeCodePk)
            .orElseThrow(CodeNotFoundException::new);
    }
}

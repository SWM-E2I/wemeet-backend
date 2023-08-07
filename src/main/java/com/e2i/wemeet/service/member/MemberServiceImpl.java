package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberMbtiRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberNicknameRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CodeRepository codeRepository;

    @Override
    public Long createMember(final CreateMemberRequestDto requestDto) {
        Code code = getCode(requestDto.collegeInfo().collegeCode());

        Member member = memberRepository.save(requestDto.toEntity(code));
        return member.getMemberId();
    }

    @Override
    public void updateNickname(Long memberId, UpdateMemberNicknameRequestDto requestDto) {

    }

    @Override
    public void updateMbti(Long memberId, UpdateMemberMbtiRequestDto requestDto) {

    }

    @Transactional(readOnly = true)
    @Override
    public MemberDetailResponseDto readMemberDetail(Long memberId) {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public MemberInfoResponseDto readMemberInfo(Long memberId) {
        return null;
    }

    @Override
    public MemberRoleResponseDto readMemberRole(MemberPrincipal memberPrincipal) {
        return null;
    }

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

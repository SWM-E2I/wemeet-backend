package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberMbtiRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberNicknameRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.aws.s3.S3Service;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // TODO :: service refactoring
    @Override
    public Long createMember(CreateMemberRequestDto requestDto) {

        return null;
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

        // member.delete();
    }
}

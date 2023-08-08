package com.e2i.wemeet.service.member;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.time.LocalDateTime;

public interface MemberService {

    /*
     * 회원 가입
     */
    Long createMember(CreateMemberRequestDto requestDto);

    /*
     * 마이페이지 상세 정보 조회
     */
    MemberDetailResponseDto readMemberDetail(Long memberId);

    /*
     * 사용자 Role + Team 여부 조회
     */
    MemberRoleResponseDto readMemberRole(MemberPrincipal memberPrincipal);

    /*
     * 사용자 정보 수정
     * */
    void updateMember(Long memberId, UpdateMemberRequestDto requestDto);

    /*
     * 회원 탈퇴
     * */
    void deleteMember(Long memberId, LocalDateTime deletedAt);
}

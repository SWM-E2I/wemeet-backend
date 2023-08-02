package com.e2i.wemeet.service.member;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;

public interface MemberService {

    /*
     * Member 생성
     */
    Long createMember(CreateMemberRequestDto requestDto);

    /*
     * Member 수정
     */
    void modifyMember(Long memberId, ModifyMemberRequestDto requestDto);

    /*
     * 마이페이지 상세 정보 조회
     */

    MemberDetailResponseDto getMemberDetail(Long memberId);

    /*
     * 사용자 정보 조회 (프로필 이미지, 인증 여부...)
     */
    MemberInfoResponseDto getMemberInfo(Long memberId);

    /*
     * 사용자 Role + Team 여부 조회
     */
    RoleResponseDto getMemberRole(Long memberId);

    /*
     * 회원 탈퇴
     * */
    void deleteMember(Long memberId);
}

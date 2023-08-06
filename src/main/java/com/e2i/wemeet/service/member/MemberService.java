package com.e2i.wemeet.service.member;

import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberMbtiRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberNicknameRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberRoleResponseDto;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    /*
     * 회원 가입
     */
    Long createMember(CreateMemberRequestDto requestDto);

    /*
     * 닉네임 수정
     */
    void updateNickname(Long memberId, UpdateMemberNicknameRequestDto requestDto);

    /*
     * MBTI 수정
     * */
    void updateMbti(Long memberId, UpdateMemberMbtiRequestDto requestDto);

    /*
     * 마이페이지 상세 정보 조회
     */
    MemberDetailResponseDto readMemberDetail(Long memberId);

    /*
     * 사용자 정보 조회 (프로필 이미지, 인증 여부...)
     */
    MemberInfoResponseDto readMemberInfo(Long memberId);

    /*
     * 사용자 Role + Team 여부 조회
     */
    MemberRoleResponseDto readMemberRole(MemberPrincipal memberPrincipal);

    /*
     * 회원 탈퇴
     * */
    void deleteMember(Long memberId, LocalDateTime deletedAt);

    /*
     * 프로필 이미지 업로드
     * */
    void uploadProfileImage(Long memberId, MultipartFile file);
}

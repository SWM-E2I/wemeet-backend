package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.MemberPreferenceResponseDto;
import java.util.List;

public interface MemberService {

    /*
     * memberId를 사용한 Member 검색
     */
    Member findMemberById(Long memberId);

    /*
     * Member 생성
     */
    Long createMember(CreateMemberRequestDto requestDto, List<Code> interestCode,
        List<Code> preferenceMeetingTypeCode);

    /*
     * Member 수정
     */
    void modifyMember(Long memberId, ModifyMemberRequestDto requestDto, List<Code> modifyCode);

    /*
     * 선호 상대 정보 수정
     */
    void modifyPreference(Long memberId, ModifyMemberPreferenceRequestDto requestDto,
        List<Code> modifyCode);

    /*
     * 마이페이지 상세 정보 조회
     */

    MemberDetailResponseDto getMemberDetail(Long memberId);

    /*
     * 사용자 정보 조회 (프로필 이미지, 인증 여부...)
     */
    MemberInfoResponseDto getMemberIndo(Long memberId);

    /*
     * 사용자 선호 상대 조회
     */
    MemberPreferenceResponseDto getMemberPrefer(Long memberId);

}

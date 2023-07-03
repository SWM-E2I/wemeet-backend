package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import java.util.List;

public interface MemberService {

    /*
     * memberId를 사용한 Member 검색
     */
    Member findMemberById(Long memberId);

    /*
     * Member 생성
     */
    Member createMember(CreateMemberRequestDto requestDto);

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
     * 메일 저장
     */
    void saveMail(Long memberId, String mail);

}

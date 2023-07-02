package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;

public interface MemberService {

    /*
     * memberId를 사용한 Member 검색
     */
    Member findMemberById(Long memberId);

    /*
     * Member 생성
     */
    Member createMember(CreateMemberRequestDto requestDto);

}

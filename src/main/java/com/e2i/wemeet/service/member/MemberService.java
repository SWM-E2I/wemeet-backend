package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;

public interface MemberService {

    /*
     * memberId를 사용한 Member 검색
     */
    Member findMemberById(Long memberId);

}

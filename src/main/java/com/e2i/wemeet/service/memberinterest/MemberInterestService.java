package com.e2i.wemeet.service.memberinterest;

import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import java.util.List;

public interface MemberInterestService {

    List<MemberInterest> findMemberInterestByMemberId(Long memberId);
}

package com.e2i.wemeet.domain.memberinterest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    List<MemberInterest> findByMemberMemberId(Long memberId);
}

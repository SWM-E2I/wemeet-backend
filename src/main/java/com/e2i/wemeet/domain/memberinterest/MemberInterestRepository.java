package com.e2i.wemeet.domain.memberinterest;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    List<MemberInterest> findByMemberMemberId(Long memberId);

    @Modifying(clearAutomatically = true)
    void deleteAllByMemberMemberId(Long memberId);
}

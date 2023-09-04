package com.e2i.wemeet.domain.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m join fetch m.collegeInfo.collegeCode where m.memberId = :memberId")
    Optional<Member> findByIdFetchCode(@Param("memberId") Long memberId);

    @Query("select m.credit from Member m where m.memberId = :memberId")
    Optional<Integer> findCreditByMemberId(@Param("memberId") Long memberId);

}
package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.member.data.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.memberId = :memberId and m.deletedAt is null")
    Optional<Member> findByMemberId(@Param("memberId") Long memberId);

    @Query("select m from Member m where m.phoneNumber = :phoneNumber and m.deletedAt is null")
    Optional<Member> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select m from Member m where m.email = :email and m.deletedAt is null")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m join fetch m.collegeInfo.collegeCode where m.memberId = :memberId and m.deletedAt is null")
    Optional<Member> findByIdFetchCode(@Param("memberId") Long memberId);

    @Query("select m.credit from Member m where m.memberId = :memberId and m.deletedAt is null")
    Optional<Integer> findCreditByMemberId(@Param("memberId") Long memberId);

    @Query("select m.role from Member m where m.memberId = :memberId and m.deletedAt is null")
    Optional<Role> findRoleByMemberId(@Param("memberId") Long memberId);

}
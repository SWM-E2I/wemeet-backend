package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, PersistLoginRepository {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m join fetch m.collegeInfo.collegeCode where m.memberId = :memberId")
    Optional<Member> findByIdFetchCode(Long memberId);
}
package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, PersistLoginRepository {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findByCollegeInfoMail(String mail);

    Optional<Member> findByNicknameAndMemberCode(String nickname, String memberCode);
}
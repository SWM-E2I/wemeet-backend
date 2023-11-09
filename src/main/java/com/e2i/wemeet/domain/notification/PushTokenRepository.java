package com.e2i.wemeet.domain.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PushTokenRepository extends JpaRepository<PushToken, String> {

    @Query("select p.token from PushToken p where p.member.memberId is not null")
    List<String> findAllMemberTokens();

    @Query("""
        select p.token
        from PushToken p
        join Member m on m.memberId = p.member.memberId
        where p.member.memberId is not null
        and m.role = 'ROLE_MEMBER'
        """)
    List<String> findTokensOfMemberWithoutTeam();
}
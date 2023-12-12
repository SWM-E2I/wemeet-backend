package com.e2i.wemeet.domain.notification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PushTokenRepository extends JpaRepository<PushToken, String> {

    @Query("select p from PushToken p where p.token = :token")
    Optional<PushToken> findByToken(@Param("token") String token);

    @Query("select p.token from PushToken p where p.member.memberId is not null")
    List<String> findAllMemberTokens();

    @Query("""
        select p.token
        from PushToken p
        join Member m on m.memberId = p.member.memberId
        where p.member.memberId is not null
        and m.role = 'USER'
        """)
    List<String> findTokensOfMemberWithoutTeam();
}

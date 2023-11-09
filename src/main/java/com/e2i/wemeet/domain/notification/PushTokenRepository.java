package com.e2i.wemeet.domain.notification;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PushTokenRepository extends JpaRepository<PushToken, String> {

    @Query("select p from PushToken p where p.token = :token")
    Optional<PushToken> findByToken(@Param("token") String token);

}
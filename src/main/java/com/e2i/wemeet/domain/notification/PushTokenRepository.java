package com.e2i.wemeet.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushToken, String> {

}
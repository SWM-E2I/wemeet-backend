package com.e2i.wemeet.service.heart;

import java.time.LocalDateTime;

public interface HeartService {

    void sendHeart(Long memberId, Long partnerTeamId, LocalDateTime requestTime);
}

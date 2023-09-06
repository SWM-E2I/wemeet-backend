package com.e2i.wemeet.service.heart;

import com.e2i.wemeet.dto.response.heart.ReceivedHeartResponseDto;
import com.e2i.wemeet.dto.response.heart.SentHeartResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface HeartService {

    void sendHeart(Long memberId, Long partnerTeamId, LocalDateTime requestTime);

    SentHeartResponseDto getSentHeart(Long memberId, LocalDateTime requestTime);

    List<ReceivedHeartResponseDto> getReceivedHeart(Long memberId, LocalDateTime requestTime);
}

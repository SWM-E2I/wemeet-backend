package com.e2i.wemeet.domain.heart;

import com.e2i.wemeet.dto.dsl.HeartTeamData;
import java.time.LocalDateTime;
import java.util.List;

public interface HeartCustomRepository {

    List<HeartTeamData> findSentHeart(Long teamId, LocalDateTime requestTime);

    List<HeartTeamData> findReceivedHeart(Long teamId, LocalDateTime requestTime);
}

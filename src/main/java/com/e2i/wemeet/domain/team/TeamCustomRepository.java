package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.dto.dsl.HeartTeamData;
import java.time.LocalDateTime;
import java.util.List;

public interface TeamCustomRepository {


    List<TeamImageData> findTeamWithTeamImages(Long teamId);

    List<HeartTeamData> findSentHeart(Long teamId, LocalDateTime requestTime);

    List<HeartTeamData> findReceivedHeart(Long teamId, LocalDateTime requestTime);
}

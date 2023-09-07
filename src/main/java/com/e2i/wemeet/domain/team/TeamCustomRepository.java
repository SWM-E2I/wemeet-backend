package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import java.util.List;

public interface TeamCustomRepository {


    List<TeamImageData> findTeamWithTeamImages(Long teamId);
}

package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import java.util.List;

public interface TeamService {

    /*
     * 팀 생성
     */
    Long createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList);
}

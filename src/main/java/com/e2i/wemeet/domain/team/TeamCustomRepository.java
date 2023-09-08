package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.e2i.wemeet.dto.dsl.TeamInformationDto;
import com.e2i.wemeet.dto.response.LeaderResponseDto;
import java.util.List;
import java.util.Optional;

public interface TeamCustomRepository {

    /*
     * 팀 이미지 조회
     * */
    List<TeamImageData> findTeamImagesByTeamId(Long teamId);

    /*
     * 팀 리더 조회
     * */
    Optional<LeaderResponseDto> findLeaderByTeamId(Long teamId);

    /*
     * 팀 정보 조회
     * */
    Optional<TeamInformationDto> findTeamInformationByTeamId(Long memberLeaderId, Long teamId);

}

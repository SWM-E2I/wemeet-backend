package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface TeamService {

    /*
     * 팀 생성
     */
    Long createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList, HttpServletResponse response);


    /*
     * 팀 정보 수정
     */
    void modifyTeam(Long memberId, ModifyTeamRequestDto modifyTeamRequestDto,
        List<Code> teamPreferenceMeetingTypeList);

    /*
     * 마이 팀 정보 조회
     */
    MyTeamDetailResponseDto getMyTeamDetail(Long memberId);

    /*
     * 팀원 초대
     */
    void inviteTeam(Long memberId, InviteTeamRequestDto inviteTeamRequestDto);

    /*
     * 초대 수락 or 거절
     */
    void takeAcceptStatus(Long memberId, Long invitationId, boolean accepted);
}

package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.dto.response.team.TeamManagementResponseDto;
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
     * 팀원 목록 조회
     */
    TeamManagementResponseDto getTeamMemberList(Long memberId);

    /*
     * 팀 삭제
     */
    void deleteTeam(Long memberId, HttpServletResponse response);

    /*
     * 팀원 삭제
     */
    void deleteTeamMember(Long managerId, Long memberId);
}

package com.e2i.wemeet.service.team;

import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TeamService {

    /*
     * 팀 생성
     */
    void createTeam(Long memberId, CreateTeamRequestDto createTeamRequestDto,
        List<MultipartFile> images);

    /*
     * 팀 정보 수정
     */
    void updateTeam(Long memberId, UpdateTeamRequestDto updateTeamRequestDto);

    /*
     * 마이 팀 정보 조회
     */
    MyTeamDetailResponseDto readTeam(Long memberId);


    /*
     * 팀 삭제
     */
    void deleteTeam(Long memberId);

}

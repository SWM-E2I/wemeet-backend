package com.e2i.wemeet.controller.team;


import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamResponseDto;
import com.e2i.wemeet.dto.response.team.TeamDetailResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.service.team.TeamService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/v1/team")
@RestController
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseDto<Void> createTeam(@MemberId Long memberId,
        @RequestPart("data") @Valid CreateTeamRequestDto createTeamRequestDto,
        @RequestPart("images") List<MultipartFile> images) {
        teamService.createTeam(memberId, createTeamRequestDto, images);

        return ResponseDto.success("Create Team Success");
    }

    @IsManager
    @PutMapping
    public ResponseDto<Void> updateTeam(@MemberId Long memberId,
        @RequestPart("data") @Valid UpdateTeamRequestDto createTeamRequestDto,
        @RequestPart("images") List<MultipartFile> images) {
        teamService.updateTeam(memberId, createTeamRequestDto, images);

        return ResponseDto.success("Update Team Success");
    }


    @GetMapping
    public ResponseDto<MyTeamResponseDto> readTeam(@MemberId Long memberId) {
        MyTeamResponseDto result = teamService.readTeam(memberId);

        return ResponseDto.success("Get My Team Detail Success", result);
    }

    @GetMapping("/{teamId}")
    public ResponseDto<TeamDetailResponseDto> readTeamById(@MemberId Long memberId, @PathVariable Long teamId) {
        TeamDetailResponseDto result = teamService.readByTeamId(memberId, teamId);

        return ResponseDto.success("Get Team Detail Success", result);
    }

    @IsManager
    @DeleteMapping
    public ResponseDto<Void> deleteTeam(@MemberId Long memberId) {
        teamService.deleteTeam(memberId);

        return ResponseDto.success("Delete Team Success");
    }
}
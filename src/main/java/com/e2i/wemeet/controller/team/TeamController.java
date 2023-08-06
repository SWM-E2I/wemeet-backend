package com.e2i.wemeet.controller.team;


import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.UpdateTeamRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/team")
@RestController
public class TeamController {

    private final TeamService teamService;
    private final CodeService codeService;

    // TODO :: service refactoring
    @PostMapping
    public ResponseDto<Void> createTeam(@MemberId Long memberId,
        @RequestBody @Valid CreateTeamRequestDto createTeamRequestDto) {
        teamService.createTeam(memberId, createTeamRequestDto);

        return ResponseDto.success("Create Team Success");
    }

    // TODO :: service refactoring
    @IsManager
    @PutMapping
    public ResponseDto<Void> modifyTeam(@MemberId Long memberId,
        @RequestBody @Valid UpdateTeamRequestDto updateTeamRequestDto) {
        teamService.updateTeam(memberId, updateTeamRequestDto);

        return ResponseDto.success("Modify Team Success");
    }

    // TODO :: service refactoring
    @GetMapping
    public ResponseDto<MyTeamDetailResponseDto> readTeam(@MemberId Long memberId) {
        MyTeamDetailResponseDto result = teamService.readTeam(memberId);

        return ResponseDto.success("Get My Team Detail Success", result);
    }

    // TODO :: service refactoring
    @IsManager
    @DeleteMapping
    public ResponseDto<Void> deleteTeam(@MemberId Long memberId) {
        teamService.deleteTeam(memberId);

        return ResponseDto.success("Delete Team Success");
    }
}
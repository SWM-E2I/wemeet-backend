package com.e2i.wemeet.controller.team;


import com.e2i.wemeet.config.security.manager.IsManager;
import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping
    public ResponseDto<Long> createTeam(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid CreateTeamRequestDto createTeamRequestDto,
        HttpServletResponse response) {
        List<Code> teamPreferenceMeetingList = codeService.findCodeList(
            createTeamRequestDto.preferenceMeetingTypeList());
        Long teamId = teamService.createTeam(memberPrincipal.getMemberId(), createTeamRequestDto,
            teamPreferenceMeetingList, response);

        return
            new ResponseDto(ResponseStatus.SUCCESS, "Create Team Success", teamId);
    }

    @IsManager
    @PutMapping
    public ResponseDto<Void> modifyTeam(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyTeamRequestDto modifyTeamRequestDto) {
        List<Code> teamPreferenceMeetingList = codeService.findCodeList(
            modifyTeamRequestDto.preferenceMeetingTypeList());
        teamService.modifyTeam(memberPrincipal.getMemberId(), modifyTeamRequestDto,
            teamPreferenceMeetingList);

        return
            new ResponseDto(ResponseStatus.SUCCESS, "Modify Team Success", null);
    }

    @GetMapping
    public ResponseDto<MyTeamDetailResponseDto> getMyTeamDetail(
        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MyTeamDetailResponseDto result = teamService.getMyTeamDetail(memberPrincipal.getMemberId());

        return
            new ResponseDto(ResponseStatus.SUCCESS, "Get  My Team Detail Success", result);
    }
}

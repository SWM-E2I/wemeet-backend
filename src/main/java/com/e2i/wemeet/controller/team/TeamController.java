package com.e2i.wemeet.controller.team;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.ResponseStatus;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
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
        @RequestBody @Valid CreateTeamRequestDto createTeamRequestDto) {
        List<Code> teamPreferenceMeetingList = codeService.findCodeList(
            createTeamRequestDto.preferenceMeetingTypeList());
        Long teamId = teamService.createTeam(memberPrincipal.getMemberId(), createTeamRequestDto,
            teamPreferenceMeetingList);

        return
            new ResponseDto(ResponseStatus.SUCCESS, "Create Team Success", teamId);
    }
}

package com.e2i.wemeet.controller.team;


import com.e2i.wemeet.config.security.manager.IsManager;
import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.dto.request.team.CreateTeamRequestDto;
import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;
import com.e2i.wemeet.dto.request.team.ModifyTeamRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.team.MyTeamDetailResponseDto;
import com.e2i.wemeet.dto.response.team.TeamManagementResponseDto;
import com.e2i.wemeet.service.code.CodeService;
import com.e2i.wemeet.service.team.TeamInvitationService;
import com.e2i.wemeet.service.team.TeamService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/team")
@RestController
public class TeamController {

    private final TeamService teamService;
    private final TeamInvitationService teamInvitationService;
    private final CodeService codeService;

    @PostMapping
    public ResponseDto<Long> createTeam(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid CreateTeamRequestDto createTeamRequestDto,
        HttpServletResponse response) {
        List<Code> teamPreferenceMeetingList = codeService.findCodeList(
            createTeamRequestDto.preferenceMeetingTypeList());
        Long teamId = teamService.createTeam(memberPrincipal.getMemberId(), createTeamRequestDto,
            teamPreferenceMeetingList, response);

        return ResponseDto.success("Create Team Success", teamId);
    }

    @IsManager
    @PutMapping
    public ResponseDto<Void> modifyTeam(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid ModifyTeamRequestDto modifyTeamRequestDto) {
        List<Code> teamPreferenceMeetingList = codeService.findCodeList(modifyTeamRequestDto.preferenceMeetingTypeList());
        teamService.modifyTeam(memberPrincipal.getMemberId(), modifyTeamRequestDto, teamPreferenceMeetingList);

        return ResponseDto.success("Modify Team Success");
    }

    @GetMapping
    public ResponseDto<MyTeamDetailResponseDto> getMyTeamDetail(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        MyTeamDetailResponseDto result = teamService.getMyTeamDetail(memberPrincipal.getMemberId());

        return ResponseDto.success("Get My Team Detail Success", result);
    }

    @IsManager
    @PostMapping("/invitation")
    public ResponseDto<Void> inviteTeamMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @RequestBody @Valid InviteTeamRequestDto inviteTeamRequestDto) {
        teamInvitationService.inviteTeam(memberPrincipal.getMemberId(), inviteTeamRequestDto);

        return ResponseDto.success("Invite Team Member Success");
    }

    @PutMapping("/invitation/{invitationId}")
    public ResponseDto<Void> setInvitationStatus(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("invitationId") Long invitationId,
        @RequestParam("accepted") Boolean accepted) {
        teamInvitationService.takeAcceptStatus(memberPrincipal.getMemberId(), invitationId, accepted);

        return ResponseDto.success("Set Invitation Success");
    }

    @IsManager
    @GetMapping("/member")
    public ResponseDto<TeamManagementResponseDto> getTeamMemberList(@AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        TeamManagementResponseDto result = teamService.getTeamMemberList(memberPrincipal.getMemberId());

        return ResponseDto.success("Get Team Member List Success", result);
    }

    @IsManager
    @DeleteMapping
    public ResponseDto<Void> deleteTeam(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        HttpServletResponse response) {
        teamService.deleteTeam(memberPrincipal.getMemberId(), response);

        return ResponseDto.success("Delete Team Success");
    }

    @IsManager
    @DeleteMapping("/member/{memberId}")
    public ResponseDto<Void> deleteTeamMember(@AuthenticationPrincipal MemberPrincipal memberPrincipal,
        @PathVariable("memberId") Long memberId) {
        teamService.deleteTeamMember(memberPrincipal.getMemberId(), memberId);

        return ResponseDto.success("Delete TeamMember Success");
    }
}
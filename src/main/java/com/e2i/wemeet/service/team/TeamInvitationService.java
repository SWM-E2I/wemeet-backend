package com.e2i.wemeet.service.team;

import com.e2i.wemeet.dto.request.team.InviteTeamRequestDto;

public interface TeamInvitationService {

    /*
     * 팀원 초대
     */
    void inviteTeam(Long memberId, InviteTeamRequestDto inviteTeamRequestDto);

    /*
     * 초대 수락 or 거절
     */
    void takeAcceptStatus(Long memberId, Long invitationId, boolean accepted);

}

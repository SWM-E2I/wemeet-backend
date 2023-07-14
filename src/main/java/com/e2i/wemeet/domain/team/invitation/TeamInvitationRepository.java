package com.e2i.wemeet.domain.team.invitation;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {

    Optional<TeamInvitation> findByMemberMemberIdAndTeamTeamIdAndAcceptStatus(Long memberId,
        Long teamId, InvitationAcceptStatus acceptStatus);

    Optional<TeamInvitation> findByTeamInvitationIdAndMemberMemberId(
        Long invitationId, Long memberId);
}

package com.e2i.wemeet.domain.meeting;

import static com.e2i.wemeet.domain.team.QTeam.team;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.dsl.QTeamCheckProxy;
import com.e2i.wemeet.dto.dsl.TeamCheckProxy;
import com.e2i.wemeet.dto.response.meeting.ReceivedMeetingResponseDto;
import com.e2i.wemeet.dto.response.meeting.SentMeetingResponseDto;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MeetingReadRepositoryImpl implements MeetingReadRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Team findTeamReferenceByLeaderId(Long memberLeaderId) {
        Long teamId = findTeamIdByLeaderId(memberLeaderId);

        return entityManager.getReference(Team.class, teamId);
    }

    @Override
    public Long findTeamIdByLeaderId(Long memberLeaderId) {
        Optional<TeamCheckProxy> teamCheckProxy = findTeamCheckProxyByLeaderId(memberLeaderId);
        return getTeamIdWithCheckValid(teamCheckProxy);
    }

    @Override
    public Team findTeamReferenceById(Long teamId) {
        Optional<TeamCheckProxy> teamCheckProxy = findTeamCheckProxyByTeamId(teamId);
        getTeamIdWithCheckValid(teamCheckProxy);

        return entityManager.getReference(Team.class, teamId);
    }

    @Override
    public List<SentMeetingResponseDto> findSentRequestList(Long memberId) {
        return null;
    }

    @Override
    public List<ReceivedMeetingResponseDto> findReceiveRequestList(Long memberId) {
        return null;
    }

    private Optional<TeamCheckProxy> findTeamCheckProxyByLeaderId(final Long leaderId) {
        return Optional.ofNullable(
            queryFactory
                .select(new QTeamCheckProxy(team.teamId, team.deletedAt))
                .from(team)
                .where(team.teamLeader.memberId.eq(leaderId))
                .fetchFirst()
        );
    }

    private Long getTeamIdWithCheckValid(final Optional<TeamCheckProxy> teamCheckProxy) {
        return teamCheckProxy
            .orElseThrow(TeamNotFoundException::new)
            .checkValid()
            .getTeamId();
    }

    private Optional<TeamCheckProxy> findTeamCheckProxyByTeamId(final Long teamId) {
        return Optional.ofNullable(
            queryFactory
                .select(new QTeamCheckProxy(team.teamId, team.deletedAt))
                .from(team)
                .where(team.teamId.eq(teamId))
                .fetchFirst()
        );
    }
}

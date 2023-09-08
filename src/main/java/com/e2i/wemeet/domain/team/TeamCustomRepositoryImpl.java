package com.e2i.wemeet.domain.team;

import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.team.data.TeamImageData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory queryFactory;
    private static final LocalTime boundaryTime = LocalTime.of(23, 11);

    @Override
    public List<TeamImageData> findTeamWithTeamImages(Long teamId) {
        return queryFactory
            .select(Projections.constructor(TeamImageData.class, teamImage.teamImageUrl.as("url")))
            .from(teamImage)
            .where(teamImage.team.teamId.eq(teamId))
            .orderBy(teamImage.sequence.asc())
            .fetch();
    }
}

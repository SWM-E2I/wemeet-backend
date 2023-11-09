package com.e2i.wemeet.domain.member.persist;

import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.notification.QPushToken.pushToken;
import static com.e2i.wemeet.domain.team.QTeam.team;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersistLoginRepositoryImpl implements PersistLoginRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PersistResponseDto> findPersistResponseById(Long memberId) {
        PersistData findPersistData = queryFactory
            .select(Projections.constructor(
                PersistData.class,
                member.nickname,
                member.email,
                member.profileImage,
                team.teamId
            ))
            .from(member)
            .leftJoin(member.team, team).on(team.deletedAt.isNull())
            .where(
                member.memberId.eq(memberId),
                member.deletedAt.isNull()
            )
            .fetchOne();

        List<String> pushTokens = queryFactory.select(pushToken.token)
            .from(pushToken)
            .join(pushToken.member, member)
            .where(pushToken.member.memberId.eq(memberId))
            .fetch();

        if (findPersistData == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(PersistResponseDto.of(findPersistData, pushTokens));
    }
}

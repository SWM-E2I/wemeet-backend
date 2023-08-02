package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// TODO :: service refactoring
@Repository
@RequiredArgsConstructor
public class PersistLoginRepositoryImpl implements PersistLoginRepository {

    private final JPAQueryFactory queryFactory;

    // TODO :: service refactoring
    @Override
    public PersistResponseDto findPersistResponseById(Long memberId) {
//        Optional<PersistLoginData> persistLoginData = Optional.ofNullable(queryFactory
//            .select(Projections.constructor(
//                PersistLoginData.class,
//                member.nickname,
//                member.collegeInfo.mail.as("email"),
//                team.teamId
//            ))
//            .from(member)
//            .leftJoin(member.team, team)
//            .where(member.memberId.eq(memberId))
//            .fetchOne());

        return null;
    }
}

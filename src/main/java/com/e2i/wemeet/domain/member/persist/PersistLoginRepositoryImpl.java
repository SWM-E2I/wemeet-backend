package com.e2i.wemeet.domain.member.persist;

import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.profileimage.QProfileImage.profileImage;
import static com.e2i.wemeet.domain.team.QTeam.team;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersistLoginRepositoryImpl implements PersistLoginRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PersistResponseDto findPersistResponseById(Long memberId) {
        Optional<PersistLoginData> persistLoginData = Optional.ofNullable(queryFactory
            .select(Projections.constructor(
                PersistLoginData.class,
                member.nickname,
                member.collegeInfo.mail.as("email"),
                member.preference,
                profileImage,
                team.teamId
            ))
            .from(member)
            .leftJoin(member.team, team)
            .leftJoin(profileImage)
            .on(profileImage.member.eq(member).and(profileImage.isMain.eq(true)))
            .where(member.memberId.eq(memberId))
            .fetchOne());

        return persistLoginData
            .orElseThrow(MemberNotFoundException::new)
            .toPersistResponseDto();
    }
}

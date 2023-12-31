package com.e2i.wemeet.domain.heart;

import static com.e2i.wemeet.domain.heart.QHeart.heart;
import static com.e2i.wemeet.domain.member.QMember.member;
import static com.e2i.wemeet.domain.team.QTeam.team;
import static com.e2i.wemeet.domain.team_image.QTeamImage.teamImage;

import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.team.data.suggestion.TeamLeaderData;
import com.e2i.wemeet.dto.dsl.HeartTeamData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class HeartCustomRepositoryImpl implements HeartCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final BlockRepository blockRepository;

    @Override
    public List<HeartTeamData> findSentHeart(Long teamId,
        LocalDateTime requestedTime) {
        LocalDateTime beforeTime = requestedTime.minusDays(1);

        // 차단 목록 조회
        List<Long> blockMemberIds = blockRepository.findBlockMemberIdsByTeamId(teamId);

        return queryFactory
            .select(
                Projections.constructor(HeartTeamData.class, heart.partnerTeam.teamId,
                    heart.partnerTeam.memberNum,
                    heart.partnerTeam.region, teamImage.teamImageUrl.as("teamMainImageUrl"),
                    heart.createdAt,
                    Projections.constructor(TeamLeaderData.class, member.nickname,
                        member.mbti,
                        member.profileImage.basicUrl.as("profileImageUrl"),
                        member.collegeInfo.collegeCode.codeValue.as("college"),
                        member.collegeInfo.admissionYear.as("admissionYear"),
                        member.email.isNotNull().as("emailAuthenticated")
                    )))
            .from(heart)
            .join(team).on(heart.team.teamId.eq(team.teamId))
            .join(heart.partnerTeam.teamLeader, member)
            .join(teamImage).on(teamImage.team.teamId.eq(heart.partnerTeam.teamId))
            .where(
                team.teamId.eq(teamId),
                team.deletedAt.isNull(),
                heart.createdAt.between(beforeTime, requestedTime),
                member.memberId.notIn(blockMemberIds),
                teamImage.sequence.eq(1)
            )
            .fetch();
    }

    @Override
    public List<HeartTeamData> findReceivedHeart(Long teamId,
        LocalDateTime requestedTime) {
        LocalDateTime beforeTime = requestedTime.minusDays(1);

        // 차단 목록 조회
        List<Long> blockMemberIds = blockRepository.findBlockMemberIdsByTeamId(teamId);

        return queryFactory
            .select(
                Projections.constructor(HeartTeamData.class, heart.team.teamId,
                    heart.team.memberNum,
                    heart.team.region, teamImage.teamImageUrl.as("teamMainImageUrl"),
                    heart.createdAt,
                    Projections.constructor(TeamLeaderData.class, member.nickname, member.mbti,
                        member.profileImage.basicUrl.as("profileImageUrl"),
                        member.collegeInfo.collegeCode.codeValue.as("college"),
                        member.collegeInfo.admissionYear.as("admissionYear"),
                        member.email.isNotNull().as("emailAuthenticated"))))
            .from(heart)
            .join(team).on(heart.partnerTeam.teamId.eq(team.teamId))
            .join(heart.team.teamLeader, member)
            .on(heart.team.teamLeader.memberId.eq(member.memberId))
            .join(teamImage).on(teamImage.team.teamId.eq(heart.team.teamId))
            .where(
                team.teamId.eq(teamId),
                team.deletedAt.isNull(),
                heart.createdAt.between(beforeTime, requestedTime),
                member.memberId.notIn(blockMemberIds),
                teamImage.sequence.eq(1)
            )
            .fetch();
    }
}

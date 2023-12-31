package com.e2i.wemeet.domain.member;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockRepository extends JpaRepository<Block, Long> {

    // 차단 목록 조회
    @Query("select b from Block b join fetch b.blockMember where b.member.memberId = :memberId")
    List<Block> findAllByMemberId(@Param("memberId") Long memberId);

    // 차단한 사용자 목록 조회 (ID)
    @Query("select b.blockMember.memberId from Block b where b.member.memberId = :memberId")
    List<Long> findBlockMemberIds(@Param("memberId") Long memberId);

    // 차단한 사용자 목록 조회 (ID)
    @Query("""
            select b.blockMember.memberId
            from Block b
            join Team t on t.teamLeader.memberId = b.member.memberId
            where t.teamId = :teamId
        """)
    List<Long> findBlockMemberIdsByTeamId(@Param("teamId") Long teamId);

}

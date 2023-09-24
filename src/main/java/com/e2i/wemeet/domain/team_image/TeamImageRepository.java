package com.e2i.wemeet.domain.team_image;

import com.e2i.wemeet.domain.team.Team;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamImageRepository extends JpaRepository<TeamImage, Long> {

    /*
     * 팀 이미지 전체 삭제
     */
    void deleteAllByTeamTeamId(Long teamId);

    @Query("SELECT t FROM Team t JOIN t.teamLeader m WHERE m.memberId = :memberId")
    Optional<Team> findTeamByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT ti FROM TeamImage ti JOIN ti.team t WHERE t.teamId = :teamId")
    List<TeamImage> findTeamImagesByTeamId(@Param("teamId") Long teamId);

    int countByTeamTeamId(Long teamId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TeamImage ti WHERE ti.teamImageUrl IN :teamImageUrls")
    void deleteAllByTeamImageUrl(@Param("teamImageUrls") Collection<String> teamImageUrls);

}

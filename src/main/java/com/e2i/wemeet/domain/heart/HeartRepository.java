package com.e2i.wemeet.domain.heart;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    @Query("select h from Heart h where h.team.teamId = :teamId and h.createdAt between :startTime and :endTime")
    Optional<Heart> findTodayHeart(@Param("teamId") Long teamId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}

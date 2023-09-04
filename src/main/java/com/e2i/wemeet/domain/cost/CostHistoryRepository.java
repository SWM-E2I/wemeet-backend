package com.e2i.wemeet.domain.cost;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CostHistoryRepository extends JpaRepository<CostHistory, Long> {

    @Query("select ch from CostHistory ch where ch.member.memberId = :memberId")
    List<CostHistory> findAllByMemberId(@Param("memberId") Long memberId);

}

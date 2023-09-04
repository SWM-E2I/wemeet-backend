package com.e2i.wemeet.domain.cost;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CostRepository extends JpaRepository<Cost, Long> {

    @Query("select c.value from Cost c where c.type = :type")
    Optional<Integer> findValueByType(@Param(value = "type") String type);

}

package com.e2i.wemeet.domain.cost;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostRepository extends JpaRepository<Cost, Long> {

    Optional<Integer> findValueByType(String type);

}

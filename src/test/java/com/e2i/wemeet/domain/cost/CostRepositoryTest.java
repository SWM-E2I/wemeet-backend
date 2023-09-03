package com.e2i.wemeet.domain.cost;

import static com.e2i.wemeet.domain.cost.Spent.MEETING_ACCEPT;

import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CostRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private CostRepository costRepository;

    @DisplayName("Cost 타입 이름으로 value를 조회할 수 있다.")
    @Test
    void findValueByType() {
        // given
        costRepository.save(new Cost(MEETING_ACCEPT));
        entityManager.flush();
        entityManager.clear();

        // when
        Integer costValue = costRepository.findValueByType(MEETING_ACCEPT.name())
            .orElseThrow();

        // then
        Assertions.assertThat(costValue).isGreaterThan(1);
    }
}
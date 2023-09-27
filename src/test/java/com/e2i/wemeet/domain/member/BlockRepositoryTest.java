package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlockRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlockRepository blockRepository;

    @DisplayName("차단 목록을 조회할 수 있다.")
    @Test
    void findAllByMemberId() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member rim = memberRepository.save(MemberFixture.RIM.create(WOMANS_CODE));
        blockRepository.save(new Block(kai, seyun));
        blockRepository.save(new Block(kai, rim));

        // when
        List<Block> findBlock = blockRepository.findAllByMemberId(kai.getMemberId());

        // then
        assertThat(findBlock).hasSize(2)
            .extracting("member", "blockMember")
            .containsExactly(
                tuple(kai, seyun),
                tuple(kai, rim)
            );
    }

}
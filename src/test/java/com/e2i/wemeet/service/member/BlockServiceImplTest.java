package com.e2i.wemeet.service.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.e2i.wemeet.domain.member.Block;
import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.support.fixture.MemberFixture;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class BlockServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockService blockService;

    @DisplayName("다른 사용자를 차단할 수 있다.")
    @Test
    void block() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));

        // when
        Long blockMemberId = blockService.block(kai.getMemberId(), seyun.getMemberId());

        // then
        List<Block> findBlocks = blockRepository.findAllByMemberId(kai.getMemberId());
        assertThat(blockMemberId).isEqualTo(seyun.getMemberId());
        assertThat(findBlocks).hasSize(1)
            .extracting("member", "blockMember")
            .contains(
                tuple(kai, seyun)
            );
    }

    @DisplayName("차단한 사용자 목록을 조회할 수 있다.")
    @Test
    void readBlock() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        Member rim = memberRepository.save(MemberFixture.RIM.create(WOMANS_CODE));
        blockRepository.save(new Block(kai, seyun));
        blockRepository.save(new Block(kai, rim));

        // when
        List<Long> blockIdList = blockService.readBlockList(kai.getMemberId());

        // then
        assertThat(blockIdList).hasSize(2)
            .containsExactly(
                seyun.getMemberId(),
                rim.getMemberId()
            );
    }

}
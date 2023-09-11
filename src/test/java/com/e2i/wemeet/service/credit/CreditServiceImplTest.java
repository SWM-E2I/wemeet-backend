package com.e2i.wemeet.service.credit;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CreditServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CreditService creditService;

    @DisplayName("멤버의 크레딧을 가져올 수 있다.")
    @Test
    void getCredit() {
        // given
        final int credit = 100;
        Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));

        // when
        int findCredit = creditService.getCredit(kai.getMemberId());

        // then
        assertThat(findCredit).isEqualTo(credit);
    }

    @DisplayName("존재하지 않는 멤버 ID라면 크레딧을 가져올 수 없다.")
    @Test
    void getCreditWithInvalidMemberId() {
        // given
        final int credit = 100;
        final Long invalidMemberId = 999L;
        Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, credit));

        // when & then
        assertThatThrownBy(() -> creditService.getCredit(invalidMemberId))
            .isInstanceOf(MemberNotFoundException.class);
    }

}
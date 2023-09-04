package com.e2i.wemeet.service.cost;

import static com.e2i.wemeet.domain.cost.Spent.MEETING_REQUEST;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.e2i.wemeet.domain.cost.CostHistory;
import com.e2i.wemeet.domain.cost.CostHistoryRepository;
import com.e2i.wemeet.domain.cost.Earn;
import com.e2i.wemeet.domain.cost.Payment;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CostServiceTest extends AbstractServiceTest {

    @Autowired
    private CostService costService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CostHistoryRepository costHistoryRepository;

    @DisplayName("유저의 크레딧이 차감된다.")
    @Test
    void spend() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        SpendEvent spendEvent = SpendEvent.of(MEETING_REQUEST, memberId);

        //when
        costService.spend(spendEvent);

        //then
        Integer memberCredit = memberRepository.findCreditByMemberId(memberId).orElseThrow();
        assertThat(memberCredit).isLessThan(credit);
    }

    @DisplayName("크레딧 차감 내역이 저장된다.")
    @Test
    void spendHistory() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        SpendEvent spendEvent = SpendEvent.of(MEETING_REQUEST, memberId);

        //when
        costService.spend(spendEvent);

        //then
        List<CostHistory> costHistories = costHistoryRepository.findAllByMemberId(memberId);
        assertThat(costHistories).hasSize(1)
            .extracting(CostHistory::getCostType, CostHistory::getDetail)
            .contains(
                tuple("SPENT", MEETING_REQUEST.name())
            );
        assertThat(costHistories.get(0).getCostValue()).isGreaterThan(1);
    }

    @DisplayName("유저의 크레딧이 증가된다.")
    @Test
    void earn() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        EarnEvent earnEvent = EarnEvent.of(Earn.EVENT, 10, memberId);

        //when
        costService.earn(earnEvent);

        //then
        Integer memberCredit = memberRepository.findCreditByMemberId(memberId).orElseThrow();
        assertThat(memberCredit).isGreaterThan(credit);
    }

    @DisplayName("크레딧 획득 내역이 저장된다.")
    @Test
    void earnHistory() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        EarnEvent earnEvent = EarnEvent.of(Earn.EVENT, 10, memberId);

        //when
        costService.earn(earnEvent);

        //then
        List<CostHistory> costHistories = costHistoryRepository.findAllByMemberId(memberId);
        assertThat(costHistories).hasSize(1)
            .extracting(CostHistory::getCostType, CostHistory::getDetail, CostHistory::getCostValue)
            .contains(
                tuple("EARN", Earn.EVENT.name(), 10)
            );
    }

    @DisplayName("유저가 금액을 결제하면 크레딧이 증가한다.")
    @Test
    void payment() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        PaymentEvent paymentEvent = PaymentEvent.of(Payment.PAYMENT_5900, memberId);

        //when
        costService.payment(paymentEvent);

        //then
        Integer memberCredit = memberRepository.findCreditByMemberId(memberId).orElseThrow();
        assertThat(memberCredit).isGreaterThan(credit);
    }

    @DisplayName("결제 내역이 저장된다.")
    @Test
    void paymentHistory() {
        //given
        final int credit = 50;
        Long memberId = memberRepository.save(KAI.create_credit(credit)).getMemberId();
        PaymentEvent paymentEvent = PaymentEvent.of(Payment.PAYMENT_5900, memberId);

        //when
        costService.payment(paymentEvent);

        //then
        List<CostHistory> costHistories = costHistoryRepository.findAllByMemberId(memberId);
        assertThat(costHistories).hasSize(1)
            .extracting(CostHistory::getCostType, CostHistory::getDetail)
            .contains(
                tuple("PAYMENT", Payment.PAYMENT_5900.name())
            );
        assertThat(costHistories.get(0).getCostValue()).isGreaterThan(1);
    }

}
package com.e2i.wemeet.service.cost;

import com.e2i.wemeet.domain.cost.CostHistory;
import com.e2i.wemeet.domain.cost.CostHistoryRepository;
import com.e2i.wemeet.domain.cost.CostRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.notfound.CostNotFoundException;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.meeting.MeetingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CostService {

    private static final String SPEND_LOG_FORMAT = "SPEND COST EVENT :: MemberId: {}, CostType: {}, Cost: {}";
    private static final String EARN_LOG_FORMAT = "EARN COST EVENT :: MemberId: {}, CostType: {}, Cost: {}";

    private final MemberRepository memberRepository;
    private final CostRepository costRepository;
    private final CostHistoryRepository costHistoryRepository;

    @EventListener(classes = MeetingEvent.class)
    public void meetingSpend(final MeetingEvent event) {
        spend(event.spendEvent());
    }

    @EventListener(classes = SpendEvent.class)
    public void spend(final SpendEvent event) {
        Integer cost = costRepository.findValueByType(event.type().name())
            .orElseThrow(CostNotFoundException::new);

        Member member = memberRepository.findById(event.memberId())
            .orElseThrow(MemberNotFoundException::new);

        member.useCredit(cost);

        costHistoryRepository.save(new CostHistory(member, event.type(), cost));
        log.info(SPEND_LOG_FORMAT, member.getMemberId(), event.type().name(), cost);
    }

    @EventListener(classes = EarnEvent.class)
    public void earn(final EarnEvent event) {
        Member member = memberRepository.findById(event.memberId())
            .orElseThrow(MemberNotFoundException::new);

        member.addCredit(event.value());

        costHistoryRepository.save(new CostHistory(member, event.type(), event.value()));
        log.info(EARN_LOG_FORMAT, member.getMemberId(), event.type().name(), event.value());
    }

    @EventListener(classes = PaymentEvent.class)
    public void payment(final PaymentEvent event) {
        Integer cost = costRepository.findValueByType(event.type().name())
            .orElseThrow(CostNotFoundException::new);

        Member member = memberRepository.findById(event.memberId())
            .orElseThrow(MemberNotFoundException::new);

        member.addCredit(cost);

        costHistoryRepository.save(new CostHistory(member, event.type(), cost));
        log.info(EARN_LOG_FORMAT, member.getMemberId(), event.type().name(), cost);
    }

}

package com.e2i.wemeet.service.member;

import static com.e2i.wemeet.domain.cost.Earn.RECOMMEND;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.cost.EarnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecommendServiceImpl implements RecommendService {

    private static final int RECOMMEND_COST = 20;

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void recommend(final Long memberId, final String recommenderPhone) {
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
        Member recommender = memberRepository.findByPhoneNumber(recommenderPhone)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();
        member.registerRecommender(recommenderPhone);

        publisher.publishEvent(
            EarnEvent.of(RECOMMEND, RECOMMEND_COST, recommender.getMemberId())
        );
    }
}

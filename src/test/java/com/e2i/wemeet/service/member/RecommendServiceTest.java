package com.e2i.wemeet.service.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.JEONGYEOL;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.SEYUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.badrequest.MemberHasBeenDeletedException;
import com.e2i.wemeet.exception.badrequest.RecommenderAlreadyExist;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RecommendServiceTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RecommendService recommendService;

    @DisplayName("추천인 전화번호를 입력하면 추천인에게 20코인을 지급한다.")
    @Test
    void recommend() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        final int before = kai.getCredit();

        setAuthentication(seyun.getMemberId(), "USER");

        // when
        recommendService.recommend(seyun.getMemberId(), kai.getPhoneNumber());

        // then
        final int after = kai.getCredit();
        final String recommenderPhone = seyun.getRecommenderPhone();
        final String kaiPhone = kai.getPhoneNumber();

        assertThat(after - before).isEqualTo(20);
        assertThat(recommenderPhone).isEqualTo(kaiPhone);
    }

    @DisplayName("이미 추천인을 입력했을경우, 더 이상 추천인 입력 기능을 수행할 수 없다.")
    @Test
    void recommendMoreThanTwice() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member jeongyeol = memberRepository.save(JEONGYEOL.create(KOREA_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        setAuthentication(seyun.getMemberId(), "USER");

        // when
        recommendService.recommend(seyun.getMemberId(), kai.getPhoneNumber());

        // then
        assertThatThrownBy(() -> recommendService.recommend(seyun.getMemberId(), jeongyeol.getPhoneNumber()))
            .isInstanceOf(RecommenderAlreadyExist.class)
            .hasMessage("recommender.already.exist");
    }

    @DisplayName("추천인이 삭제된 유저일 경우, 추천인 입력 기능을 수행할 수 없다.")
    @Test
    void recommendWithDeletedMember() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Member seyun = memberRepository.save(SEYUN.create(KOREA_CODE));
        LocalDateTime deleteTime = LocalDateTime.of(2023, 9, 10, 13, 0, 0);
        kai.delete(deleteTime);
        entityManager.flush();
        entityManager.clear();

        setAuthentication(seyun.getMemberId(), "USER");

        // when
        assertThatThrownBy(() -> recommendService.recommend(seyun.getMemberId(), kai.getPhoneNumber()))
            .isInstanceOf(MemberHasBeenDeletedException.class)
            .hasMessage("member.has.been.deleted");
    }

}
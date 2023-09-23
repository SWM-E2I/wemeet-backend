package com.e2i.wemeet.config.security.manager;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.service.admin.TestAuthorizationService;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CostAuthorizationManagerTest extends AbstractServiceTest {

    @Autowired
    private TestAuthorizationService testAuthorizationService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사용자가 보유한 크레딧이 요청에 필요한 크레딧보다 많다면 요청에 성공한다.")
    @Test
    void memberHasSomeCreditToRequest() {
        // given
        Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, 30));
        setAuthentication(kai.getMemberId(), Role.USER.name());

        // when & then
        assertDoesNotThrow(() -> testAuthorizationService.requireCredit());
    }

    @DisplayName("사용자가 보유한 크레딧이 요청에 필요한 크레딧보다 부족하다면 요청을 수행할 수 없다.")
    @Test
    void memberHasNotEnoughCreditToRequest() {
        // given
        Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, 1));
        setAuthentication(kai.getMemberId(), Role.USER.name());

        // when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCredit())
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }

    @DisplayName("사용자의 권한이 요청에 필요한 권한보다 낮을 경우 요청을 수행할 수 없다.")
    @Test
    void memberHasLowRole() {
        Member kai = memberRepository.save(KAI.create_credit(ANYANG_CODE, 100));
        setAuthentication(kai.getMemberId(), Role.USER.name());

        // when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCreditAndAdmin())
            .isExactlyInstanceOf(UnAuthorizedRoleException.class);
    }

}
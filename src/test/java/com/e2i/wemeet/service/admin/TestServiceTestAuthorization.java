package com.e2i.wemeet.service.admin;

import static com.e2i.wemeet.domain.member.data.Role.ADMIN;
import static com.e2i.wemeet.domain.member.data.Role.MANAGER;
import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class CustomBeanAuthorizationTest extends AbstractIntegrationTest {

    @Autowired
    private TestAuthorizationService testAuthorizationService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("매니저 권한이 필요한 행동을 매니저 권한을 가진 사용자가 수행할 수 있다.")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void requireManagerRole() {
        assertDoesNotThrow(() -> testAuthorizationService.requireManagerRole());
    }

    @DisplayName("매니저 권한이 필요한 행동은 매니저보다 하위 권한을 가진 사용자가 수행할 수 없다.")
    @WithCustomMockUser
    @Test
    void requireManagerRoleFail() {
        assertThatThrownBy(() -> testAuthorizationService.requireManagerRole())
            .isExactlyInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("요청에 필요한 크레딧보다 많은 양의 크레딧을 보유하고 있으면 성공한다.")
    @WithCustomMockUser
    @Test
    void requireCredit() {
        //given
        final Member member = KAI.create_credit(4);
        memberRepository.save(member);
        setAuthentication(member);

        //when & then
        assertDoesNotThrow(() -> testAuthorizationService.requireCredit());
    }

    @DisplayName("요청에 필요한 크레딧보다 적은 양의 크레딧을 보유하고 있으면 실패한다.")
    @WithCustomMockUser
    @Test
    void requireCreditFail() {
        //given
        final Member member = KAI.create_credit(2);
        memberRepository.save(member);
        setAuthentication(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCredit())
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }

    @DisplayName("크레딧과 권한이 충분하면 요청에 성공한다.")
    @WithCustomMockUser(role = "ADMIN")
    @Test
    void requireCreditAndAdmin() {
        //given
        final Member member = KAI.create_role_credit(ADMIN, 4);
        memberRepository.save(member);
        setAuthentication(member);

        //when & then
        assertDoesNotThrow(() -> testAuthorizationService.requireCreditAndAdmin());
    }


    @DisplayName("크레딧은 충분하지만 권한이 부족하다면 요청에 실패한다.")
    @WithCustomMockUser
    @Test
    void requireCreditAndAdminFailRole() {
        //given
        final Member member = KAI.create_role_credit(MANAGER, 4);
        memberRepository.save(member);
        setAuthentication(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCreditAndAdmin())
            .isExactlyInstanceOf(UnAuthorizedRoleException.class);
    }

    @DisplayName("권한은 충분하지만 크레딧이 부족하다면 요청에 실패한다.")
    @WithCustomMockUser(role = "ADMIN")
    @Test
    void requireCreditAndAdminFailCredit() {
        //given
        final Member member = KAI.create_role_credit(ADMIN, 2);
        memberRepository.save(member);
        setAuthentication(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCreditAndAdmin())
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }

    private void setAuthentication(Member member) {
        MemberPrincipal principal = new MemberPrincipal(member);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
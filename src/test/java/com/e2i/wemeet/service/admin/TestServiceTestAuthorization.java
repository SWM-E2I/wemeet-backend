package com.e2i.wemeet.service.admin;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.support.config.WithCustomMockUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

@DisplayName("Authorization Test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestServiceTestAuthorization {

    @Autowired
    private TestAuthorizationService testAuthorizationService;

    @Autowired
    private MemberRepository memberRepository;

    Member member;

    @BeforeAll
    void setUp() {
        member = KAI.create();
        memberRepository.save(member);
    }

    @DisplayName("매니저 권한이 필요한 메소드를 Manager 권한을 가진 사용자가 호출하면 성공한다")
    @WithCustomMockUser(role = "MANAGER")
    @Test
    void requireManagerRole() {
        assertDoesNotThrow(() -> testAuthorizationService.requireManagerRole());
    }

    @DisplayName("매니저 권한이 필요한 메소드를 User 권한을 가진 사용자가 호출하면 실패한다")
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
        if (!(member.getCredit() >= 3)) {
            member.addCredit(3);
        }
        memberRepository.save(member);

        //when & then
        assertDoesNotThrow(() -> testAuthorizationService.requireCredit());
    }

    @DisplayName("요청에 필요한 크레딧보다 적은 양의 크레딧을 보유하고 있으면 실패한다.")
    @WithCustomMockUser
    @Test
    void requireCreditFail() {
        //given
        int existCredit = member.getCredit();
        if (existCredit >= 3) {
            member.minusCredit(existCredit);
        }
        memberRepository.save(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCredit())
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }

    @DisplayName("크레딧과 권한이 충분하면 요청에 성공한다.")
    @WithCustomMockUser(role = "ADMIN")
    @Test
    void requireCreditAndAdmin() {
        //given
        if (!(member.getCredit() >= 3)) {
            member.addCredit(3);
        }
        memberRepository.save(member);

        //when & then
        assertDoesNotThrow(() -> testAuthorizationService.requireCreditAndAdmin());
    }


    @DisplayName("크레딧은 충분하지만 권한이 부족하다면 요청에 실패한다.")
    @WithCustomMockUser
    @Test
    void requireCreditAndAdminFailRole() {
        //given
        if (!(member.getCredit() >= 3)) {
            member.addCredit(3);
        }
        memberRepository.save(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCreditAndAdmin())
            .isExactlyInstanceOf(UnAuthorizedRoleException.class);
    }

    @DisplayName("권한은 충분하지만 크레딧이 부족하다면 요청에 실패한다.")
    @WithCustomMockUser(role = "ADMIN")
    @Test
    void requireCreditAndAdminFailCredit() {
        //given
        int existCredit = member.getCredit();
        if (existCredit >= 3) {
            member.minusCredit(existCredit);
        }
        memberRepository.save(member);

        //when & then
        assertThatThrownBy(() -> testAuthorizationService.requireCreditAndAdmin())
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }
}
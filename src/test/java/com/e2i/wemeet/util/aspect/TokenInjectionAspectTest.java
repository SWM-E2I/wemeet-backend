package com.e2i.wemeet.util.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.TokenInjector;
import com.e2i.wemeet.support.fixture.MemberFixture;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenInjectionAspectTest {

    @Mock
    private TokenInjector tokenInjector;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TokenInjectionAspect tokenInjectionAspect;

    @Test
    void testInjectUserTokenAdvice() {
        // given
        Member member = MemberFixture.KAI.create();

        // when
        tokenInjectionAspect.injectTokenAdvice(member.getMemberId());

        // then
        verify(tokenInjector).injectToken(any(HttpServletResponse.class),
            any(MemberPrincipal.class));
    }

    @Test
    void testInjectManagerTokenAdvice() {
        // given
        MemberFixture.KAI.create_with_id(1L);

        // when
        tokenInjectionAspect.injectManagerTokenAdvice(1L);

        // then
        verify(tokenInjector).injectToken(any(HttpServletResponse.class),
            any(Payload.class));
    }
}

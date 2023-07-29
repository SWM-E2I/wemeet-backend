package com.e2i.wemeet.util.aspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.Payload;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.support.fixture.MemberFixture;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
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
        HttpServletResponse response = mock(HttpServletResponse.class);
        Member member = MemberFixture.KAI.create();

        JoinPoint joinPoint = mock(JoinPoint.class);
        Object[] args = new Object[]{member, response};
        when(joinPoint.getArgs()).thenReturn(args);

        // when
        tokenInjectionAspect.injectUserTokenAdvice(joinPoint, member);

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

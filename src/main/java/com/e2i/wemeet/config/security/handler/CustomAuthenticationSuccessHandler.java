package com.e2i.wemeet.config.security.handler;

import static com.e2i.wemeet.dto.response.ResponseStatus.SUCCESS;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.config.security.token.TokenInjector;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.dto.response.credential.SmsCredentialResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenInjector tokenInjector;

    /*
     * SmsCredentialAuthenticationProvider 에서 반환
     * CASE
     * 1. SMS 인증에 성공했지만 회원 가입 되어있지 않음
     * 2. SMS 인증에 성공했고 회원 가입이 되어 있는 경우 -> JWT 발급
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();

        // JWT 토큰 발급
        if (principal.isRegistered()) {
            tokenInjector.injectToken(response, principal);
        }
        writeResponse(response, principal);
    }

    /*
     * Write Response Body
     * */
    private void writeResponse(HttpServletResponse response, MemberPrincipal principal)
        throws IOException {
        log.info("Login Request Success - {}", principal.toString());

        SmsCredentialResponse data = SmsCredentialResponse.of(principal);
        ResponseDto result = new ResponseDto(SUCCESS, "인증에 성공하였습니다.", data);

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsString(result).getBytes());
    }
}

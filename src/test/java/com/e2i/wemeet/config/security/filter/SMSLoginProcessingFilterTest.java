package com.e2i.wemeet.config.security.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.e2i.wemeet.config.security.token.JwtEnv;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.dto.request.LoginRequestDto;
import com.e2i.wemeet.dto.request.credential.CredentialRequestDto;
import com.e2i.wemeet.support.config.AbstractIntegrationTest;
import com.e2i.wemeet.support.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("SMS 인증 테스트")
class SMSLoginProcessingFilterTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("올바른 인증번호를 입력하면 SMS 인증에 성공한다.")
    @Test
    void smsLoginProcess() throws Exception {
        // given - 인증 번호 발급
        final String phone = "+821088990011";
        CredentialRequestDto credentialRequestDto = new CredentialRequestDto(phone);

        mvc.perform(
            post("/v1/auth/phone/issue")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(credentialRequestDto))
        );
        Integer credential = Integer.valueOf(redisTemplate.opsForValue().get(phone));

        // when - 로그인 요청
        LoginRequestDto loginRequestDto = new LoginRequestDto(phone, credential);

        ResultActions perform = mvc.perform(
            post("/v1/auth/phone/validate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(loginRequestDto))
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("인증에 성공하였습니다."),
            jsonPath("$.data").isNotEmpty()
        );
    }

    @DisplayName("SMS 인증에 성공한 사용자가 이미 가입된 사용자라면 AccessToken 과 RefreshToken이 반환된다.")
    @Test
    void smsLoginProcessToken() throws Exception {
        // given - 가입 & 인증 번호 발급
        Member member = MemberFixture.KAI.create();
        memberRepository.save(member);

        final String phone = member.getPhoneNumber();
        CredentialRequestDto credentialRequestDto = new CredentialRequestDto(phone);

        mvc.perform(
            post("/v1/auth/phone/issue")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(credentialRequestDto))
        );
        Integer credential = Integer.valueOf(redisTemplate.opsForValue().get(phone));

        // when - 로그인 요청
        LoginRequestDto loginRequestDto = new LoginRequestDto(phone, credential);

        ResultActions perform = mvc.perform(
            post("/v1/auth/phone/validate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(loginRequestDto))
        );

        // then
        perform.andExpectAll(
            status().isOk(),
            header().exists(JwtEnv.ACCESS.getKey()),
            cookie().exists(JwtEnv.REFRESH.getKey()),
            jsonPath("$.status").value("SUCCESS"),
            jsonPath("$.message").value("인증에 성공하였습니다."),
            jsonPath("$.data").isNotEmpty()
        );
    }

    @DisplayName("인증번호가 일치하지 않으면 SMS 인증에 실패한다.")
    @Test
    void smsLoginFail() throws Exception {
        // given - 인증 번호 발급
        final String phone = "+821088990011";
        CredentialRequestDto credentialRequestDto = new CredentialRequestDto(phone);

        mvc.perform(
            post("/v1/auth/phone/issue")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(credentialRequestDto))
        );

        // when / then - 로그인 요청
        LoginRequestDto loginRequestDto = new LoginRequestDto(phone, 100_000);

        ResultActions perform = mvc.perform(
            post("/v1/auth/phone/validate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(loginRequestDto))
        );

        perform.andExpect(status().isUnauthorized());
    }
}
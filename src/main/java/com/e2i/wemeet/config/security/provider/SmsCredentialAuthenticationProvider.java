package com.e2i.wemeet.config.security.provider;

import com.e2i.wemeet.dto.request.LoginRequestDto;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidSmsCredentialException;
import com.e2i.wemeet.service.credential.sms.SmsCredentialService;
import com.e2i.wemeet.util.encryption.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * 사용자로부터 받은 SMS 인증 번호를 검증한다.
 * */
@RequiredArgsConstructor
public class SmsCredentialAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final SmsCredentialService smsCredentialService;

    // 인증 처리
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        final LoginRequestDto loginRequest = (LoginRequestDto) authentication.getPrincipal();
        final String phone = loginRequest.phone();
        final String credential = String.valueOf(authentication.getCredentials());

        // 사용자가 입력한 인증 번호와 실제 발급한 인증 번호가 같은지 확인
        if (!smsCredentialService.matches(phone, credential)) {
            throw new InvalidSmsCredentialException(ErrorCode.INVALID_SMS_CREDENTIAL);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(
            EncryptionUtils.hashData(phone));

        // 인증된 객체 반환 authenticated == true
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    // Provider 가 인증 처리를 할 수 있는지 check
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

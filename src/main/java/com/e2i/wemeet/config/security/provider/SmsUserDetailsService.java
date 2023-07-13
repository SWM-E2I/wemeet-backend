package com.e2i.wemeet.config.security.provider;

import com.e2i.wemeet.config.security.model.MemberPrincipal;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class SmsUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /*
     * username == phone
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByPhoneNumber(username).orElse(null);

        // SMS 인증을 요청한 사용자가 회원가입이 되어있지 않을 경우
        if (member == null) {
            return new MemberPrincipal();
        }
        return new MemberPrincipal(member);
    }
}

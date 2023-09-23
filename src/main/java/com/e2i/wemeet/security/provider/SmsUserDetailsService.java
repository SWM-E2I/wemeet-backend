package com.e2i.wemeet.security.provider;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.security.model.MemberPrincipal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class SmsUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /*
     * username == phone
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByPhoneNumber(username);

        if (member.isPresent() && member.get().getDeletedAt() != null) {
            memberRepository.delete(member.get());
            member = Optional.empty();
        }

        // SMS 인증을 요청한 사용자가 회원가입이 되어있지 않을 경우
        return member.map(MemberPrincipal::new)
            .orElseGet(MemberPrincipal::new);
    }
}

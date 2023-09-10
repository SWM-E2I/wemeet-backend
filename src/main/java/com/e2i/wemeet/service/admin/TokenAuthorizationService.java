package com.e2i.wemeet.service.admin;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenAuthorizationService {

    private final MemberRepository memberRepository;

    public Role getMemberRoleByMemberId(Long memberId) {
        Role memberRole = memberRepository.findRoleByMemberId(memberId);

        if (memberRole == null) {
            throw new MemberNotFoundException();
        }

        return memberRole;
    }
}

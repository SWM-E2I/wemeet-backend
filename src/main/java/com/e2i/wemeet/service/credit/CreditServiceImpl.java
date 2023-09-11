package com.e2i.wemeet.service.credit;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CreditServiceImpl implements CreditService {

    private final MemberRepository memberRepository;

    @Override
    public int getCredit(final Long memberId) {
        return memberRepository.findCreditByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }

}

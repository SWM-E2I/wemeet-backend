package com.e2i.wemeet.service.memberinterest;

import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.memberinterest.MemberInterestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberInterestServiceImpl implements MemberInterestService {

    private final MemberInterestRepository memberInterestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberInterest> findMemberInterestByMemberId(Long memberId) {
        return memberInterestRepository.findByMemberMemberId(memberId);
    }
}

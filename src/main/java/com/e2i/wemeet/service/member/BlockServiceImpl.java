package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.BlockRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlockServiceImpl implements BlockService {

    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;

    @Transactional
    @Override
    public Long block(final Long memberId, final Long blockMemberId) {
        Member member = memberRepository.findByMemberId(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        Member blockMember = memberRepository.getReferenceById(blockMemberId);
        member.addBlockMember(blockMember);

        return blockMember.getMemberId();
    }

    @Override
    public List<Long> readBlockList(final Long memberId) {
        return blockRepository.findAllByMemberId(memberId).stream()
            .filter(block -> block.getBlockMember().isActive())
            .map(block -> block.getBlockMember().getMemberId())
            .toList();
    }

}

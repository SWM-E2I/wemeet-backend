package com.e2i.wemeet.service.token;

import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import com.e2i.wemeet.domain.notification.PushToken;
import com.e2i.wemeet.domain.notification.PushTokenRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class TokenServiceImpl implements TokenService {

    @Qualifier("persistLoginRepositoryImpl")
    private final PersistLoginRepository persistLoginRepository;
    private final PushTokenRepository pushTokenRepository;
    private final MemberRepository memberRepository;

    public TokenServiceImpl(PersistLoginRepository persistLoginRepository, PushTokenRepository pushTokenRepository,
        MemberRepository memberRepository) {
        this.persistLoginRepository = persistLoginRepository;
        this.pushTokenRepository = pushTokenRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public PersistResponseDto persistLogin(final Long memberId) {
        return persistLoginRepository.findPersistResponseById(memberId)
            .orElseThrow(MemberNotFoundException::new);
    }

    @Override
    public void savePushToken(final String pushToken, final Long memberId) {
        Optional<PushToken> findToken = pushTokenRepository.findByToken(pushToken);
        if (findToken.isPresent()) {
            updateMemberIfIdNotNull(findToken.get(), memberId);
            return;
        }

        PushToken token = PushToken.builder()
            .token(pushToken)
            .build();
        updateMemberIfIdNotNull(token, memberId);
        pushTokenRepository.save(token);
    }

    private void updateMemberIfIdNotNull(PushToken token, Long memberId) {
        if (memberId == null) {
            return;
        }
        memberRepository.findByMemberId(memberId)
            .ifPresent(token::setMember);
    }
}

package com.e2i.wemeet.service.token;

import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenServiceImpl implements TokenService {

    private final PersistLoginRepository persistLoginRepository;

    public TokenServiceImpl(
        @Qualifier("persistLoginRepositoryImpl") PersistLoginRepository persistLoginRepository) {
        this.persistLoginRepository = persistLoginRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public PersistResponseDto persistLogin(final Long memberId) {
        return persistLoginRepository.findPersistResponseById(memberId);
    }
}

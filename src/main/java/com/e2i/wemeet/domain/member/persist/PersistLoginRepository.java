package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;

public interface PersistLoginRepository {

    PersistResponseDto findPersistResponseById(Long memberId);
}

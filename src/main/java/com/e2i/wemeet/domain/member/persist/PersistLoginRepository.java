package com.e2i.wemeet.domain.member.persist;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import java.util.Optional;

public interface PersistLoginRepository {

    Optional<PersistResponseDto> findPersistResponseById(Long memberId);
}

package com.e2i.wemeet.service.token;

import com.e2i.wemeet.dto.response.persist.PersistResponseDto;

public interface TokenService {

    PersistResponseDto persistLogin(Long memberId);
}

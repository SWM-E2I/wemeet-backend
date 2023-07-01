package com.e2i.wemeet.service.admin;

import com.e2i.wemeet.config.security.manager.CreditAuthorize;
import com.e2i.wemeet.config.security.manager.IsManager;
import com.e2i.wemeet.domain.member.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestAuthorizationService {

    // 팀장이어야 함
    @IsManager
    public void requireManagerRole() {
    }

    // 3 크레딧이 있어야 함
    @CreditAuthorize(3)
    public void requireCredit() {
    }

    // 3 크레딧이 있어야 함
    // 팀장여야 함
    @CreditAuthorize(value = 3, role = Role.ADMIN)
    public void requireCreditAndAdmin() {
    }
}

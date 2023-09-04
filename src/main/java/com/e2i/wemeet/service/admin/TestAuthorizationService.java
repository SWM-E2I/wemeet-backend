package com.e2i.wemeet.service.admin;

import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.security.manager.CostAuthorize;
import com.e2i.wemeet.security.manager.IsManager;
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
    @CostAuthorize(3)
    public void requireCredit() {
    }

    // 3 크레딧이 있어야 함
    // 팀장여야 함
    @CostAuthorize(value = 3, role = Role.ADMIN)
    public void requireCreditAndAdmin() {
    }
}

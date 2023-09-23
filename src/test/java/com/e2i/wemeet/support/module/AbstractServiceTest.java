package com.e2i.wemeet.support.module;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.code.GroupCodeRepository;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.service.sns.AwsSnsService;
import com.e2i.wemeet.support.fixture.code.CodeFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest
public abstract class AbstractServiceTest {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected GroupCodeRepository groupCodeRepository;

    @Autowired
    protected CodeRepository codeRepository;

    @MockBean
    private AwsSnsService awsSnsService;

    protected Code KOREA_CODE;
    protected Code ANYANG_CODE;
    protected Code INHA_CODE;
    protected Code WOMANS_CODE;
    protected Code HANYANG_CODE;

    @BeforeEach
    void setUpCode() {
        KOREA_CODE = codeRepository.findByCodeValue(CodeFixture.KOREA_UNIVERSITY.getCodeValue())
            .orElseThrow();
        ANYANG_CODE = codeRepository.findByCodeValue(CodeFixture.ANYANG_UNIVERSITY.getCodeValue())
            .orElseThrow();
        INHA_CODE = codeRepository.findByCodeValue(CodeFixture.INHA_UNIVERSITY.getCodeValue())
            .orElseThrow();
        WOMANS_CODE = codeRepository.findByCodeValue(CodeFixture.WOMANS_UNIVERSITY.getCodeValue())
            .orElseThrow();
        HANYANG_CODE = codeRepository.findByCodeValue(CodeFixture.HANYANG_UNIVERSITY.getCodeValue())
            .orElseThrow();
    }

    protected void setAuthentication(Long memberId, String role) {
        MemberPrincipal principal = new MemberPrincipal(memberId, role);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

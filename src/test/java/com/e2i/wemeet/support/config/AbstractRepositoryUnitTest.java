package com.e2i.wemeet.support.config;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.code.GroupCode;
import com.e2i.wemeet.domain.code.GroupCodeRepository;
import com.e2i.wemeet.support.fixture.code.CodeFixture;
import com.e2i.wemeet.support.fixture.code.GroupCodeFixture;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RepositoryTest
public abstract class AbstractRepositoryUnitTest {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected GroupCodeRepository groupCodeRepository;

    @Autowired
    protected CodeRepository codeRepository;

    protected Code KOREA_CODE;
    protected Code ANYANG_CODE;
    protected Code INHA_CODE;
    protected Code WOMANS_CODE;
    protected Code HANYANG_CODE;

    @BeforeEach
    void setUpCode() {
        GroupCode collegeGroup = groupCodeRepository.save(GroupCodeFixture.COLLEGE_CODE.create());

        KOREA_CODE = codeRepository.save(CodeFixture.KOREA_UNIVERSITY.create(collegeGroup));
        ANYANG_CODE = codeRepository.save(CodeFixture.ANYANG_UNIVERSITY.create(collegeGroup));
        INHA_CODE = codeRepository.save(CodeFixture.INHA_UNIVERSITY.create(collegeGroup));
        WOMANS_CODE = codeRepository.save(CodeFixture.WOMANS_UNIVERSITY.create(collegeGroup));
        HANYANG_CODE = codeRepository.save(CodeFixture.HANYANG_UNIVERSITY.create(collegeGroup));
    }

    @AfterEach
    void cleanUp() {
        groupCodeRepository.deleteAll();
        codeRepository.deleteAll();
    }
}

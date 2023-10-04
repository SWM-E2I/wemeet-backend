package com.e2i.wemeet.support.module;

import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_woman;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.code.GroupCodeRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.rest.support.MultipartRequest;
import com.e2i.wemeet.security.token.Payload;
import com.e2i.wemeet.security.token.handler.AccessTokenHandler;
import com.e2i.wemeet.support.config.DatabaseCleaner;
import com.e2i.wemeet.support.fixture.code.CodeFixture;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "aws.s3.teamImageBucket=wemeet-test-bucket",
    "aws.s3.profileImageBucket=wemeet-test-bucket"
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanUp();
    }

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected ResourceLoader resourceLoader;

    @Autowired
    protected AccessTokenHandler accessTokenHandler;

    @Autowired
    protected GroupCodeRepository groupCodeRepository;

    @Autowired
    protected TeamRepository teamRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected CodeRepository codeRepository;

    protected Code KOREA_CODE;
    protected Code ANYANG_CODE;
    protected Code WOMANS_CODE;
    protected Code HANYANG_CODE;

    @BeforeEach
    void setUpCode() {
        KOREA_CODE = codeRepository.findByCodeValue(CodeFixture.KOREA_UNIVERSITY.getCodeValue())
            .orElseThrow();
        ANYANG_CODE = codeRepository.findByCodeValue(CodeFixture.ANYANG_UNIVERSITY.getCodeValue())
            .orElseThrow();
        WOMANS_CODE = codeRepository.findByCodeValue(CodeFixture.WOMANS_UNIVERSITY.getCodeValue())
            .orElseThrow();
        HANYANG_CODE = codeRepository.findByCodeValue(CodeFixture.HANYANG_UNIVERSITY.getCodeValue())
            .orElseThrow();
    }

    protected CreationData 여자_4인팀을_생성한다(final Member member) {
        Member savedMember = memberRepository.save(member);
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(savedMember, create_3_woman()));
        String accessToken = accessTokenHandler.createToken(new Payload(savedMember.getMemberId(), "MANAGER"));

        return CreationData.of(accessToken, savedMember, team);
    }

    protected CreationData 남자_4인팀을_생성한다(final Member member) {
        Member savedMember = memberRepository.save(member);
        Team team = teamRepository.save(HONGDAE_TEAM_1.create(savedMember, create_3_man()));
        String accessToken = accessTokenHandler.createToken(new Payload(savedMember.getMemberId(), "MANAGER"));

        return CreationData.of(accessToken, savedMember, team);
    }

    protected MultipartRequest<Object> createMultiPartRequest(String partName, String resourcePath) throws IOException {
        File file = resourceLoader.getResource(resourcePath).getFile();
        return MultipartRequest.of(partName, file);
    }

    protected <T> MultipartRequest<T> createMultiPartRequest(String partName, T data) {
        return MultipartRequest.of(partName, data);
    }

    protected record CreationData(
        String accessToken,
        Member member,
        Team team
    ) {

        public static CreationData of(String accessToken, Member member, Team team) {
            return new CreationData(accessToken, member, team);
        }
    }
}

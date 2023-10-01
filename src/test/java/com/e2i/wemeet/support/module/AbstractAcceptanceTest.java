package com.e2i.wemeet.support.module;

import com.e2i.wemeet.support.config.DatabaseCleaner;
import io.restassured.RestAssured;
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
    int port;

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

    @Autowired
    protected ResourceLoader resourceLoader;

}

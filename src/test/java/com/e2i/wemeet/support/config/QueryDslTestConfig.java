package com.e2i.wemeet.support.config;

import com.e2i.wemeet.domain.member.persist.PersistLoginRepositoryImpl;
import com.e2i.wemeet.util.encryption.AdvancedEncryptionStandard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableConfigurationProperties(AdvancedEncryptionStandard.class)
@TestConfiguration
public class QueryDslTestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public PersistLoginRepositoryImpl persistLoginImpl(JPAQueryFactory jpaQueryFactory) {
        return new PersistLoginRepositoryImpl(jpaQueryFactory);
    }
}

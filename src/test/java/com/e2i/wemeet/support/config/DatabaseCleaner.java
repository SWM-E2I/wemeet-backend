package com.e2i.wemeet.support.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 데이터 베이스 초기화
@Component
@Profile("local || default")
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> deletionExclusionList = List.of(
        "GROUP_CODE",
        "CODE",
        "COST"
    );

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
            .map(entityType -> entityType.getJavaType().getDeclaredAnnotation(Table.class))
            .filter(tableMetaData -> tableMetaData != null && !deletionExclusionList.contains(tableMetaData.name()))
            .map(Table::name)
            .toList();
    }

    @Transactional
    public void cleanUp() {
        entityManager.flush();
        // 참조 무결성 제약 조건 임시 해제
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 모든 테이블 삭제
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // 참조 무결성 제약 조건 활성화
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

}

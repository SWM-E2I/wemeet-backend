package com.e2i.wemeet.support.fixture.code;

import com.e2i.wemeet.domain.code.GroupCode;
import com.e2i.wemeet.support.config.ReflectionUtils;
import lombok.Getter;

@Getter
public enum GroupCodeFixture {
    COLLEGE_CODE("CE", "College", "대학교명");

    private final String groupCodeId;
    private final String name;
    private final String description;

    GroupCodeFixture(String groupCodeId, String name, String description) {
        this.groupCodeId = groupCodeId;
        this.name = name;
        this.description = description;
    }

    public GroupCode create() {
        GroupCode groupCode = ReflectionUtils.createInstance(GroupCode.class);
        ReflectionUtils.setFieldValue(groupCode, "groupCodeId", this.groupCodeId);
        ReflectionUtils.setFieldValue(groupCode, "name", this.name);
        ReflectionUtils.setFieldValue(groupCode, "description", this.description);

        return groupCode;
    }
}

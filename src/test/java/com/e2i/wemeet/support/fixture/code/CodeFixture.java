package com.e2i.wemeet.support.fixture.code;

import static com.e2i.wemeet.support.fixture.code.GroupCodeFixture.COLLEGE_CODE;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.GroupCode;
import com.e2i.wemeet.support.config.ReflectionUtils;

public enum CodeFixture {
    // COLLEGE CODE
    SEOUL_UNIVERSITY(COLLEGE_CODE.create(), "051", "서울대"),
    KOREA_UNIVERSITY(COLLEGE_CODE.create(), "018", "고려대"),
    YONSEI_UNIVERSITY(COLLEGE_CODE.create(), "085", "연세대"),
    HANYANG_UNIVERSITY(COLLEGE_CODE.create(), "131", "한양대"),
    SOGANG_UNIVERSITY(COLLEGE_CODE.create(), "046", "서강대"),
    SANGMYUNG_UNIVERSITY(COLLEGE_CODE.create(), "045", "상명대"),
    WOMANS_UNIVERSITY(COLLEGE_CODE.create(), "055", "서울여대"),
    KYUNGHEE_UNIVERSITY(COLLEGE_CODE.create(), "016", "경희대"),
    ANYANG_UNIVERSITY(COLLEGE_CODE.create(), "081", "안양대"),
    INCHEON_UNIVERSITY(COLLEGE_CODE.create(), "098", "인천대"),
    INHA_UNIVERSITY(COLLEGE_CODE.create(), "101", "인하대"),

    // OTHER CODE
    ;

    private final GroupCode groupCode;
    private final String codeId;
    private final String codeValue;

    CodeFixture(GroupCode groupCode, String codeId, String codeValue) {
        this.groupCode = groupCode;
        this.codeId = codeId;
        this.codeValue = codeValue;
    }

    public Code create() {
        CodePk codePk = createCodePk(this.codeId, this.groupCode.getGroupCodeId());

        return createCode(codePk, this.groupCode);
    }

    public Code create(final GroupCode groupCode) {
        CodePk codePk = createCodePk(this.codeId, groupCode.getGroupCodeId());

        return createCode(codePk, groupCode);
    }

    private CodePk createCodePk(final String codeId, final String groupCodeId) {
        CodePk codePk = ReflectionUtils.createInstance(CodePk.class);
        ReflectionUtils.setFieldValue(codePk, "codeId", codeId);
        ReflectionUtils.setFieldValue(codePk, "groupCodeId", groupCodeId);

        return codePk;
    }

    private Code createCode(CodePk codePk, GroupCode groupCode) {
        Code code = ReflectionUtils.createInstance(Code.class);
        ReflectionUtils.setFieldValue(code, "codePk", codePk);
        ReflectionUtils.setFieldValue(code, "codeValue", this.codeValue);
        ReflectionUtils.setFieldValue(code, "groupCode", groupCode);
        return code;
    }

    public GroupCode getGroupCode() {
        return groupCode;
    }

    public String getCodeId() {
        return codeId;
    }

    public String getCodeValue() {
        return codeValue;
    }
}

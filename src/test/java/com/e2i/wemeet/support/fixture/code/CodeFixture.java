package com.e2i.wemeet.support.fixture.code;

import static com.e2i.wemeet.support.fixture.code.GroupCodeFixture.COLLEGE_CODE;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.GroupCode;
import com.e2i.wemeet.support.config.ReflectionUtils;

public enum CodeFixture {
    // COLLEGE CODE
    SEOUL_UNIVERSITY(COLLEGE_CODE.create(), "001", "대학교"),
    KOREA_UNIVERSITY(COLLEGE_CODE.create(), "002", "고려대학교"),
    YONSEI_UNIVERSITY(COLLEGE_CODE.create(), "003", "연세대학교"),
    HANYANG_UNIVERSITY(COLLEGE_CODE.create(), "004", "한양대학교"),
    SOGANG_UNIVERSITY(COLLEGE_CODE.create(), "005", "서강대학교"),
    SANGMYUNG_UNIVERSITY(COLLEGE_CODE.create(), "006", "상명대학교"),
    WOMANS_UNIVERSITY(COLLEGE_CODE.create(), "007", "여자대학교"),
    KYUNGHEE_UNIVERSITY(COLLEGE_CODE.create(), "008", "경희대학교"),
    ANYANG_UNIVERSITY(COLLEGE_CODE.create(), "009", "안양대학교"),
    INCHEON_UNIVERSITY(COLLEGE_CODE.create(), "010", "인천대학교"),
    INHA_UNIVERSITY(COLLEGE_CODE.create(), "011", "인하대학교"),

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
}

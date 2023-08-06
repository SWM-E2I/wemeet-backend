package com.e2i.wemeet.domain.code;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class CodePk implements Serializable {

    @Column(length = 3, name = "code_id")
    private String codeId;

    @Column(length = 2, name = "group_code_id")
    private String groupCodeId;

    public CodePk(String codeId, String groupCodeId) {
        this.codeId = codeId;
        this.groupCodeId = groupCodeId;
    }
}

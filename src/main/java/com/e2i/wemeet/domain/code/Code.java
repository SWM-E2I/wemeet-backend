package com.e2i.wemeet.domain.code;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CODE")
@Entity
public class Code extends BaseTimeEntity implements Persistable<CodePk> {

    @EmbeddedId
    private CodePk codePk;

    @Column(nullable = false)
    private String codeValue;

    @MapsId("groupCodeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_code_id")
    private GroupCode groupCode;

    @Builder
    public Code(String codeId, String codeValue, GroupCode groupCode) {
        this.codePk = new CodePk(codeId, groupCode.getGroupCodeId());
        this.codeValue = codeValue;
        this.groupCode = groupCode;
    }

    @Override
    public CodePk getId() {
        return this.codePk;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}

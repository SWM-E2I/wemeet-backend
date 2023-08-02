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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CODE")
@Entity
public class Code extends BaseTimeEntity {

    @EmbeddedId
    private CodePk codePk;

    @Column(nullable = false)
    private String value;

    @MapsId("groupCodeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupCodeId")
    private GroupCode groupCode;

    @Builder
    public Code(CodePk codePk, String value, GroupCode groupCode) {
        this.codePk = codePk;
        this.value = value;
        this.groupCode = groupCode;
    }
}

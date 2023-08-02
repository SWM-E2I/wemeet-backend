package com.e2i.wemeet.domain.code;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_CODE")
@Entity
public class GroupCode extends BaseTimeEntity {

    @Id
    @Column(length = 4)
    private String groupCodeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

}

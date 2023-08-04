package com.e2i.wemeet.domain.code;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_CODE")
@Entity
public class GroupCode extends BaseTimeEntity implements Persistable<String> {

    @Id
    @Column(length = 4, name = "group_code_id")
    private String groupCodeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Override
    public String getId() {
        return groupCodeId;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}

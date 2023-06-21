package com.e2i.wemeet.domain.code;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GROUP_CODE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCode extends BaseTimeEntity {

  @Id
  @Column(length = 4)
  private String groupCodeId;

  @Column(nullable = false)
  private String groupCodeName;

  @Column(nullable = false)
  private String description;
}

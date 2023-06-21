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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CODE")
@Getter
@NoArgsConstructor
public class Code extends BaseTimeEntity {

  @EmbeddedId
  private CodePk codePk;

  @Column(nullable = false)
  private String codeName;

  @Column(nullable = false)
  private String description;

  @MapsId("groupCodeId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "groupCodeId")
  private GroupCode groupCode;
}

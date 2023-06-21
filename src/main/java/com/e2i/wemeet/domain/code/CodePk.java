package com.e2i.wemeet.domain.code;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CodePk implements Serializable {

  @Column(length = 4, name = "codeId")
  private String codeId;

  private String groupCodeId;
}

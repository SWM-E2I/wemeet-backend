package com.e2i.wemeet.domain.member;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class PreferenceMbti {
  private int eOrI;
  private int sOrN;
  private int tOrF;
  private int jOrP;

  @Builder
  public PreferenceMbti(int eOrI, int sOrN, int tOrF, int jOrP) {
    this.eOrI = eOrI;
    this.sOrN = sOrN;
    this.tOrF = tOrF;
    this.jOrP = jOrP;
  }
}

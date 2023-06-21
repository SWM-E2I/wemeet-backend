package com.e2i.wemeet.domain.profile_image;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PROFILE_IMAGE")
@Getter
@NoArgsConstructor
public class ProfileImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long profileImageId;

  @Column(nullable = false)
  private String basicUrl;

  @Column(nullable = false)
  private String blurUrl;

  @Column(nullable = false)
  private String lowResolutionBasicUrl;

  @Column(nullable = false)
  private String lowResolutionBlurUrl;

  @Column(nullable = false)
  private boolean isMain;

  @Column(nullable = false)
  private boolean isCertified;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberId", nullable = false)
  private Member member;

  @Builder
  public ProfileImage(Long profileImageId, String basicUrl, String blurUrl,
      String lowResolutionBasicUrl, String lowResolutionBlurUrl, boolean isMain,
      boolean isCertified) {
    this.profileImageId = profileImageId;
    this.basicUrl = basicUrl;
    this.blurUrl = blurUrl;
    this.lowResolutionBasicUrl = lowResolutionBasicUrl;
    this.lowResolutionBlurUrl = lowResolutionBlurUrl;
    this.isMain = isMain;
    this.isCertified = isCertified;
  }
}

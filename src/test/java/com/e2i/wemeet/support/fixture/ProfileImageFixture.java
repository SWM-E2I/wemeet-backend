package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.profile_image.ProfileImage;

public enum ProfileImageFixture {

    MAIN_IMAGE("basic", "blur", "lowBasic", "lowBlur", true, true, MemberFixture.KAI.create()),
    ADDITIONAL_IMAGE("basic", "blur", "lowBasic", "lowBlur", false, false,
        MemberFixture.KAI.create());

    private final String basicUrl;

    private final String blurUrl;


    private final String lowResolutionBasicUrl;


    private final String lowResolutionBlurUrl;


    private final boolean isMain;


    private final boolean isCertified;

    private final Member member;

    ProfileImageFixture(String basicUrl, String blurUrl, String lowResolutionBasicUrl,
        String lowResolutionBlurUrl, boolean isMain, boolean isCertified, Member member) {
        this.basicUrl = basicUrl;
        this.blurUrl = blurUrl;
        this.lowResolutionBasicUrl = lowResolutionBasicUrl;
        this.lowResolutionBlurUrl = lowResolutionBlurUrl;
        this.isMain = isMain;
        this.isCertified = isCertified;
        this.member = member;
    }

    public ProfileImage create() {
        return createBuilder()
            .member(member)
            .build();
    }

    public ProfileImage create(Member member) {
        return createBuilder()
            .member(member)
            .build();
    }

    private ProfileImage.ProfileImageBuilder createBuilder() {
        return ProfileImage.builder()
            .basicUrl(basicUrl)
            .blurUrl(blurUrl)
            .lowResolutionBasicUrl(lowResolutionBasicUrl)
            .lowResolutionBlurUrl(lowResolutionBlurUrl)
            .isMain(isMain)
            .isCertified(isCertified);
    }
}

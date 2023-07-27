package com.e2i.wemeet.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Preference {

    private String startPreferenceAdmissionYear;

    private String endPreferenceAdmissionYear;

    private String sameCollegeState;

    private String drinkingOption;

    private boolean isAvoidedFriends;

    @Column(length = 4)
    private String preferenceMbti;

    @Builder
    public Preference(String startPreferenceAdmissionYear, String endPreferenceAdmissionYear,
        String sameCollegeState, String drinkingOption, boolean isAvoidedFriends,
        String preferenceMbti) {
        this.startPreferenceAdmissionYear = startPreferenceAdmissionYear;
        this.endPreferenceAdmissionYear = endPreferenceAdmissionYear;
        this.sameCollegeState = sameCollegeState;
        this.drinkingOption = drinkingOption;
        this.isAvoidedFriends = isAvoidedFriends;
        this.preferenceMbti = preferenceMbti;
    }

    public boolean isComplete() {
        return
            StringUtils.hasText(this.startPreferenceAdmissionYear) &&
                StringUtils.hasText(this.endPreferenceAdmissionYear) &&
                StringUtils.hasText(this.sameCollegeState) &&
                StringUtils.hasText(this.drinkingOption) &&
                StringUtils.hasText(this.preferenceMbti);
    }
}

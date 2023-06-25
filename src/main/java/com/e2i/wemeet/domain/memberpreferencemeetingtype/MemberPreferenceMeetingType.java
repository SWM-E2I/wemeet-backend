package com.e2i.wemeet.domain.memberpreferencemeetingtype;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_PREFERENCE_MEETING_TYPE")
@Entity
public class MemberPreferenceMeetingType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberPreferenceMeetingTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupCodeId", referencedColumnName = "groupCodeId")
    @JoinColumn(name = "codeId", referencedColumnName = "codeId")
    private Code code;

    @Builder
    public MemberPreferenceMeetingType(Long memberPreferenceMeetingTypeId, Member member,
        Code code) {
        this.memberPreferenceMeetingTypeId = memberPreferenceMeetingTypeId;
        this.member = member;
        this.code = code;
    }
}

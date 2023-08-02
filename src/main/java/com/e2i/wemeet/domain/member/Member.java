package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.CryptoConverter;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.exception.badrequest.MemberHasBeenDeletedException;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 6, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Convert(converter = CryptoConverter.class)
    @Column(length = 60, unique = true, nullable = false)
    private String phoneNumber;

    @Convert(converter = CryptoConverter.class)
    @Column(length = 60, unique = true)
    private String email;

    @Embedded
    private CollegeInfo collegeInfo;

    @Column(length = 7, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Mbti mbti;

    @Column(nullable = false)
    private Integer credit;

    @Column
    private Boolean imageAuth;

    @Embedded
    private ProfileImage profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @Builder
    public Member(String nickname, Gender gender, String phoneNumber, String email, CollegeInfo collegeInfo, Mbti mbti, Integer credit,
        Boolean imageAuth,
        ProfileImage profileImage, Role role, LocalDateTime deletedAt) {
        this.nickname = nickname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
        this.credit = credit;
        this.imageAuth = imageAuth;
        this.profileImage = profileImage;
        this.role = role;
        this.deletedAt = deletedAt;
    }

    public void addCredit(int amount) {
        this.credit += amount;
    }

    public void minusCredit(int amount) {
        if (this.credit - amount < 0) {
            throw new CreditNotEnoughException();
        }
        this.credit = this.credit - amount;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }

    public void modifyMbti(Mbti mbti) {
        this.mbti = mbti;
    }

    // TODO :: refactoring
    public boolean isEmailAuthenticated() {
        return false;
    }

    public Member checkMemberValid() {
        if (this.getDeletedAt() != null) {
            throw new MemberHasBeenDeletedException();
        }
        return this;
    }

    private void validateManager() {
        if (this.role != Role.MANAGER && this.role != Role.ADMIN) {
            throw new UnAuthorizedRoleException();
        }
    }

    public void saveEmail(final String email) {
        this.email = email;
    }
}

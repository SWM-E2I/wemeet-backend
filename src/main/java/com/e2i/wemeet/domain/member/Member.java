package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.domain.base.BaseTimeEntity;
import com.e2i.wemeet.domain.base.converter.CryptoConverter;
import com.e2i.wemeet.domain.base.converter.GenderConverter;
import com.e2i.wemeet.domain.base.converter.MbtiConverter;
import com.e2i.wemeet.domain.cost.CostHistory;
import com.e2i.wemeet.domain.history.History;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.exception.badrequest.MemberHasBeenDeletedException;
import com.e2i.wemeet.exception.badrequest.ProfileImageNotExistsException;
import com.e2i.wemeet.exception.badrequest.TeamExistsException;
import com.e2i.wemeet.exception.badrequest.TeamNotExistsException;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedUnivException;
import com.e2i.wemeet.util.validator.CustomFormatValidator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "MEMBER")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(length = 5, nullable = false)
    private String nickname;

    @Convert(converter = GenderConverter.class)
    @Column(nullable = false)
    private Gender gender;

    @Convert(converter = CryptoConverter.class)
    @Column(length = 60, unique = true, nullable = false)
    private String phoneNumber;

    @Convert(converter = CryptoConverter.class)
    @Column(length = 60, unique = true)
    private String email;

    @Embedded
    private CollegeInfo collegeInfo;

    @Convert(converter = MbtiConverter.class)
    private Mbti mbti;

    @Column(nullable = false)
    private Integer credit;

    @Column(nullable = false)
    private Boolean allowMarketing;

    @Embedded
    private ProfileImage profileImage;

    @Convert(converter = CryptoConverter.class)
    @Column(length = 24)
    private String recommenderPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "teamLeader", cascade = CascadeType.PERSIST)
    private List<Team> team = new ArrayList<>();

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<CostHistory> costHistory = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<History> history = new ArrayList<>();

    @Builder
    public Member(String nickname, Gender gender, String phoneNumber, String email,
        CollegeInfo collegeInfo, Mbti mbti, Integer credit, Boolean allowMarketing,
        ProfileImage profileImage, Role role) {
        this.nickname = nickname;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
        this.credit = credit;
        this.allowMarketing = allowMarketing;
        this.profileImage = profileImage;
        this.role = role;
    }

    // 크레딧 획득
    public void addCredit(int amount) {
        this.credit += amount;
    }

    // 크레딧 소모
    public void useCredit(int amount) {
        if (this.credit - amount < 0) {
            throw new CreditNotEnoughException();
        }
        this.credit = this.credit - amount;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public boolean isEmailAuthenticated() {
        return StringUtils.hasText(this.email);
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

    public void delete(LocalDateTime deletedAt) {
        // Team 이 존재한다면 예외 발생
        List<LocalDateTime> notDeleted = this.team.stream()
            .map(Team::getDeletedAt)
            .filter(Objects::isNull)
            .toList();
        if (!notDeleted.isEmpty()) {
            throw new TeamExistsException();
        }

        this.deletedAt = deletedAt;
    }

    public void setTeam(Team team) {
        if (!this.team.contains(team)) {
            this.team.add(team);
        }
    }

    public void update(UpdateMemberRequestDto requestDto) {
        if (StringUtils.hasText(requestDto.mbti())) {
            this.mbti = Mbti.findBy(requestDto.mbti());
        }
        if (StringUtils.hasText(requestDto.nickname())) {
            String nickname = requestDto.nickname();
            CustomFormatValidator.validateNicknameFormat(nickname);
            this.nickname = nickname.trim();
        }
    }

    public void saveProfileImage(final ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    public Team getCurrentTeam() {
        return this.team.stream()
            .filter(t -> t.getDeletedAt() == null)
            .findFirst()
            .orElseThrow(TeamNotExistsException::new);
    }

    public boolean hasTeam() {
        return this.team.stream()
            .anyMatch(t -> t.getDeletedAt() == null);
    }

    public void validateTeamCreation() {
        if (this.team.stream().anyMatch(t -> t.getDeletedAt() == null)) {
            throw new TeamExistsException();
        }

        if (!isEmailAuthenticated()) {
            throw new UnAuthorizedUnivException();
        }

        if (!isProfileImageExists()) {
            throw new ProfileImageNotExistsException();
        }
    }

    public boolean isProfileImageExists() {
        return this.profileImage != null;
    }

    public String getCollegeName() {
        return this.collegeInfo.getCollegeCode().getCodeValue();
    }

    public void registerRecommender(final String recommenderPhone) {
        this.recommenderPhone = recommenderPhone;
    }
}


package com.e2i.wemeet.dto.response.persist;

import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.persist.PersistData;
import java.util.List;
import java.util.Objects;
import lombok.Builder;

@Builder
public record PersistResponseDto(
    String nickname,
    boolean emailAuthenticated,
    boolean hasMainProfileImage,
    String basicProfileImage,
    String lowProfileImage,
    boolean profileImageAuthenticated,
    boolean hasTeam,
    List<String> pushTokens
) {

    public static PersistResponseDto of(PersistData persistData, List<String> pushTokens) {
        final boolean emailAuthenticated = persistData.email() != null;
        final boolean hasTeam = persistData.teamId() != null;

        PersistResponseDtoBuilder builder = PersistResponseDto.builder()
            .nickname(persistData.nickname())
            .emailAuthenticated(emailAuthenticated)
            .hasTeam(hasTeam)
            .pushTokens(pushTokens);

        if (Objects.isNull(persistData.profileImage())) {
            return createResponseHasNoImage(builder);
        }
        return createResponse(persistData.profileImage(), builder);
    }

    private static PersistResponseDto createResponse(ProfileImage profileImage, PersistResponseDtoBuilder builder) {
        return builder
            .hasMainProfileImage(profileImage.hasProfileImage())
            .profileImageAuthenticated(profileImage.isImageAuthenticated())
            .basicProfileImage(profileImage.getBasicUrl())
            .lowProfileImage(profileImage.getLowUrl())
            .build();
    }

    private static PersistResponseDto createResponseHasNoImage(PersistResponseDtoBuilder builder) {
        return builder
            .hasMainProfileImage(false)
            .profileImageAuthenticated(false)
            .basicProfileImage(null)
            .lowProfileImage(null)
            .build();
    }

    @Override
    public String toString() {
        return "PersistResponseDto{" +
            "nickname='" + nickname + '\'' +
            ", emailAuthenticated=" + emailAuthenticated +
            ", hasMainProfileImage=" + hasMainProfileImage +
            ", basicProfileImage='" + basicProfileImage + '\'' +
            ", lowProfileImage='" + lowProfileImage + '\'' +
            ", profileImageAuthenticated=" + profileImageAuthenticated +
            ", hasTeam=" + hasTeam +
            '}';
    }
}

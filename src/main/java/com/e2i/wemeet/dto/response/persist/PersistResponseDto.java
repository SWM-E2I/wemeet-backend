package com.e2i.wemeet.dto.response.persist;

import com.e2i.wemeet.domain.member.persist.PersistData;
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
    boolean hasTeam
) {

    public static PersistResponseDto of(PersistData persistData) {
        final boolean emailAuthenticated = persistData.email() != null;
        final boolean hasTeam = persistData.teamId() != null;

        PersistResponseDtoBuilder builder = PersistResponseDto.builder()
            .nickname(persistData.nickname())
            .emailAuthenticated(emailAuthenticated)
            .hasTeam(hasTeam);

        if (Objects.isNull(persistData.profileImage())) {
            return createResponseHasNoImage(builder);
        }
        return createResponse(persistData, builder);
    }

    private static PersistResponseDto createResponse(PersistData persistData, PersistResponseDtoBuilder builder) {
        return builder
            .hasMainProfileImage(persistData.profileImage().hasProfileImage())
            .basicProfileImage(persistData.profileImage().getBasicUrl())
            .lowProfileImage(persistData.profileImage().getLowUrl())
            .profileImageAuthenticated(persistData.profileImage().getImageAuth())
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

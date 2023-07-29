package com.e2i.wemeet.service.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.persist.PersistLoginRepository;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.exception.unauthorized.AccessTokenNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenServiceImplTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        PersistLoginRepositoryRepositoryStub repositoryStub = new PersistLoginRepositoryRepositoryStub();
        tokenService = new TokenServiceImpl(repositoryStub);
    }

    @DisplayName("persist 정보를 가져오는데 성공한다.")
    @Test
    void persistLoginSuccess() {
        // given
        final Long memberId = 1L;

        // when
        PersistResponseDto persistResponseDto = tokenService.persistLogin(memberId);

        // then
        assertAll(
            () -> assertThat(persistResponseDto).isNotNull(),
            () -> assertThat(persistResponseDto.nickname()).isEqualTo("kai"),
            () -> assertThat(persistResponseDto.emailAuthenticated()).isTrue(),
            () -> assertThat(persistResponseDto.profileImageAuthenticated()).isFalse(),
            () -> assertThat(persistResponseDto.hasMainProfileImage()).isTrue(),
            () -> assertThat(persistResponseDto.hasTeam()).isTrue(),
            () -> assertThat(persistResponseDto.preferenceCompleted()).isTrue()
        );
    }

    @DisplayName("존재하지 않는 사용자의 persist 정보를 가져오는데 실패한다.")
    @Test
    void persistLoginFail() {
        // given
        final Long invalidMemberId = 999L;

        // when & then
        assertThatThrownBy(() -> tokenService.persistLogin(invalidMemberId))
            .isExactlyInstanceOf(AccessTokenNotFoundException.class);
    }

    static class PersistLoginRepositoryRepositoryStub implements PersistLoginRepository {

        @Override
        public PersistResponseDto findPersistResponseById(Long memberId) {
            if (memberId == 999L) {
                throw new AccessTokenNotFoundException();
            }

            return PersistResponseDto.builder()
                .nickname("kai")
                .preferenceCompleted(true)
                .hasMainProfileImage(true)
                .emailAuthenticated(true)
                .hasTeam(true)
                .profileImageAuthenticated(false)
                .build();
        }
    }
}
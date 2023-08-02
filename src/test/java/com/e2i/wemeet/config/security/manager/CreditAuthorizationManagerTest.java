package com.e2i.wemeet.config.security.manager;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.Role;
import com.e2i.wemeet.dto.response.persist.PersistResponseDto;
import com.e2i.wemeet.exception.unauthorized.CreditNotEnoughException;
import com.e2i.wemeet.exception.unauthorized.UnAuthorizedRoleException;
import com.e2i.wemeet.security.manager.CreditAuthorizationManager;
import com.e2i.wemeet.security.manager.CreditAuthorize;
import com.e2i.wemeet.security.model.MemberPrincipal;
import com.e2i.wemeet.security.token.Payload;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class CreditAuthorizationManagerTest {

    private CreditAuthorizationManager creditAuthorizationManager;

    @BeforeEach
    void setUp() {
        creditAuthorizationManager = new CreditAuthorizationManager(new MemberRepositoryImpl(),
            new RoleHierarchyImpl());
    }

    @DisplayName("사용자가 보유한 credit의 개수가 요청에 필요한 credit의 개수보다 많을 경우 성공한다.")
    @Test
    void verify() {
        // given
        MemberPrincipal principal = new MemberPrincipal(new Payload(1L, Role.USER.name()));
        final Authentication authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // when
        CreditAuthorize creditAuthorize = new CreditAuthorize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public int value() {
                return 5;
            }

            @Override
            public Role role() {
                return Role.USER;
            }
        };

        //then
        assertDoesNotThrow(
            () -> creditAuthorizationManager.verify(authentication, creditAuthorize));
    }

    @DisplayName("사용자가 보유한 credit의 개수가 요청에 필요한 credit의 개수보다 적을 경우 예외가 발생한다.")
    @Test
    void verify2() {
        // given
        MemberPrincipal principal = new MemberPrincipal(new Payload(1L, Role.USER.name()));
        final Authentication authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // when
        CreditAuthorize creditAuthorize = new CreditAuthorize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public int value() {
                return 15;
            }

            @Override
            public Role role() {
                return Role.USER;
            }
        };

        //then
        assertThatThrownBy(() -> creditAuthorizationManager.verify(authentication, creditAuthorize))
            .isExactlyInstanceOf(CreditNotEnoughException.class);
    }

    @DisplayName("사용자의 권한이 요청에 필요한 권한보다 낮을 경우 실패한다.")
    @Test
    void verifyFailRole() {
        // given

        MemberPrincipal principal = new MemberPrincipal(new Payload(1L, Role.USER.name()));
        final Authentication authentication =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // when
        CreditAuthorize creditAuthorize = new CreditAuthorize() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public int value() {
                return 5;
            }

            @Override
            public Role role() {
                return Role.MANAGER;
            }
        };

        //then
        assertThatThrownBy(() -> creditAuthorizationManager.verify(authentication, creditAuthorize))
            .isExactlyInstanceOf(UnAuthorizedRoleException.class);
    }

    static class MemberRepositoryImpl implements MemberRepository {

        @Override
        public Optional<Member> findByNicknameAndMemberCode(String nickname, String memberCode) {
            return Optional.of(
                Member.builder()
                    .build()
            );
        }

        @Override
        public Optional<Member> findById(Long id) {
            return Optional.of(
                Member.builder()
                    .credit(10)
                    .build()
            );
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public Optional<Member> findByPhoneNumber(String phoneNumber) {
            return Optional.empty();
        }

        @Override
        public Optional<Member> findByCollegeInfoMail(String mail) {
            return Optional.empty();
        }

        @Override
        public void flush() {
        }

        @Override
        public <S extends Member> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<Member> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Member getOne(Long aLong) {
            return null;
        }

        @Override
        public Member getById(Long aLong) {
            return null;
        }

        @Override
        public Member getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends Member> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Member> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Member> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public <S extends Member> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Member> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Member> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Member, R> R findBy(Example<S> example,
            Function<FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public <S extends Member> S save(S entity) {
            return null;
        }

        @Override
        public <S extends Member> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public List<Member> findAll() {
            return null;
        }

        @Override
        public List<Member> findAllById(Iterable<Long> longs) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(Member entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends Member> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Member> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<Member> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public PersistResponseDto findPersistResponseById(Long memberId) {
            return null;
        }
    }

    static class RoleHierarchyImpl implements RoleHierarchy {

        @Override
        public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(Role.USER.getRoleAttachedPrefix())) {
                    return List.of(
                        new SimpleGrantedAuthority(Role.GUEST.name()),
                        new SimpleGrantedAuthority(Role.USER.name()));

                } else if (authority.getAuthority().equals(Role.MANAGER.getRoleAttachedPrefix())) {
                    return List.of(
                        new SimpleGrantedAuthority(Role.GUEST.name()),
                        new SimpleGrantedAuthority(Role.USER.name()),
                        new SimpleGrantedAuthority(Role.MANAGER.name()));

                } else if (authority.getAuthority().equals(Role.ADMIN.getRoleAttachedPrefix())) {
                    return List.of(
                        new SimpleGrantedAuthority(Role.GUEST.name()),
                        new SimpleGrantedAuthority(Role.USER.name()),
                        new SimpleGrantedAuthority(Role.MANAGER.name()),
                        new SimpleGrantedAuthority(Role.ADMIN.name()));
                }
            }

            return null;
        }
    }


}
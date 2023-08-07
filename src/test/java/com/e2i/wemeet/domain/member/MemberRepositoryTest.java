package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.support.config.AbstractRepositoryUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class MemberRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 가입 요청을 통해 회원을 저장할 수 있다.")
    @Test
    void saveWithMemberRequestDto() {
        // given
        CreateMemberRequestDto requestDto = KAI.createMemberRequestDto();
        Code code = codeRepository.findByCodePk(CodePk.of(requestDto.collegeInfo().collegeCode()))
            .orElseThrow();
        Member entity = requestDto.toEntity(code);

        // when
        Member member = memberRepository.save(entity);

        // then
        assertThat(member.getMemberId()).isGreaterThan(0L);
    }

    @DisplayName("동일한 번호로 가입한 사용자가 있다면, 회원을 저장할 수 없다.")
    @Test
    void saveWithMemberRequestDto_duplicatePhone() {
        // given
        memberRepository.save(RIM.create_phone(KAI.getPhoneNumber()));

        CreateMemberRequestDto requestDto = KAI.createMemberRequestDto();
        Code code = codeRepository.findByCodePk(CodePk.of(requestDto.collegeInfo().collegeCode()))
            .orElseThrow();
        Member requestEntity = requestDto.toEntity(code);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(requestEntity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("동일한 이메일로 가입한 사용자가 있다면, 회원을 저장할 수 없다.")
    @Test
    void saveWithMemberRequestDto_duplicateEmail() {
        // given
        memberRepository.save(RIM.create_email(KAI.getEmail()));

        // when & then
        assertThatThrownBy(() -> memberRepository.save(KAI.create(ANYANG_CODE)))
            .isExactlyInstanceOf(DataIntegrityViolationException.class);
    }
}
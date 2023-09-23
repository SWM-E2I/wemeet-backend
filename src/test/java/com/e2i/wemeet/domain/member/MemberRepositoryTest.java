package com.e2i.wemeet.domain.member;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.MemberFixture.RIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.UpdateMemberRequestDto;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.support.module.AbstractRepositoryUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class MemberRepositoryTest extends AbstractRepositoryUnitTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("핸드폰 번호로 회원을 조회할 수 있다.")
    @Test
    void findByPhoneNumber() {
        // given
        final String phone = "+821077779999";
        memberRepository.save(RIM.create_phone(phone));
        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findByPhoneNumber(phone)
            .orElseThrow();

        // then
        assertThat(findMember)
            .extracting("nickname", "phoneNumber", "gender", "email")
            .contains(RIM.getNickname(), phone, RIM.getGender(), RIM.getEmail());
    }

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
        assertThat(member.getMemberId()).isPositive();
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

    @DisplayName("회원 엔티티로부터 대학교 이름을 받을 수 있다.")
    @Test
    void findCollegeNameFromMember() {
        // given
        Long savedMemberId = memberRepository.save(KAI.create()).getMemberId();
        entityManager.flush();
        entityManager.clear();

        // when
        Member member = memberRepository.findByIdFetchCode(savedMemberId)
            .orElseThrow();

        // then
        assertThat(member.getCollegeInfo().getCollegeCode().getCodeValue())
            .isEqualTo("안양대");
    }

    @DisplayName("닉네임 길이가 10자를 초과하면, 회원 정보를 수정할 수 없다.")
    @Test
    void saveFailWithOverTenLengthNickname() {
        // given
        UpdateMemberRequestDto updateRequest = new UpdateMemberRequestDto("기우미우기우미우기우미", "ENFP");
        Long savedMemberId = memberRepository.save(KAI.create(ANYANG_CODE)).getMemberId();
        entityManager.flush();
        entityManager.clear();

        // when
        Member member = memberRepository.findById(savedMemberId)
            .orElseThrow();

        // then
        assertThatThrownBy(() -> member.update(updateRequest))
            .isInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("멤버의 크레딧을 조회할 수 있다.")
    @Test
    void findCreditByMemberId() {
        // given
        final Long memberId = memberRepository.save(KAI.create_credit(ANYANG_CODE, 300))
            .getMemberId();
        entityManager.flush();
        entityManager.clear();

        // when
        final Integer credit = memberRepository.findCreditByMemberId(memberId).orElseThrow();

        // then
        assertThat(credit).isEqualTo(300);
    }
}
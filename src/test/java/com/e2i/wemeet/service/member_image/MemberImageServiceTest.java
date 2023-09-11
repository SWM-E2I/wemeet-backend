package com.e2i.wemeet.service.member_image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.service.aws.s3.S3Service;
import com.e2i.wemeet.support.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MemberImageServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MemberImageServiceImpl memberImageService;

    @DisplayName("회원의 프로필 이미지를 등록할 수 있다.")
    @Test
    void uploadProfileImage_Success() {
        // given
        Long memberId = 1L;
        Member kai = MemberFixture.KAI.create_with_id(memberId);
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpg",
            "test data".getBytes());

        when(memberRepository.findById(memberId))
            .thenReturn(Optional.of(kai));
        doNothing().when(s3Service).upload(any(MultipartFile.class), anyString(), any());

        // when
        memberImageService.uploadProfileImage(memberId, multipartFile);

        // then
        assertThat(kai.getProfileImage().getBasicUrl())
            .isNotNull()
            .containsPattern("v1/.*./basic/.*..jpg");
//        assertThat(kai.getProfileImage().getLowUrl())
//            .isNotNull()
//            .containsPattern("v1/.*./low/.*..jpg");
    }
}

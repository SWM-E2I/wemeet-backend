package com.e2i.wemeet.service.team;

import static com.e2i.wemeet.support.fixture.MemberFixture.KAI;
import static com.e2i.wemeet.support.fixture.TeamFixture.HONGDAE_TEAM_1;
import static com.e2i.wemeet.support.fixture.TeamMemberFixture.create_3_man;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team_image.TeamImage;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.support.module.AbstractServiceTest;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Profile("test")
@Transactional
class S3TeamImageServiceTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    private TeamImageService teamImageService;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.teamImageBucket}")
    public String teamBucket;

    @DisplayName("S3에 오브젝트를 업로드하고 삭제하는데 성공한다.")
        //@TestFactory
    Collection<DynamicTest> testAllFeature() {
        // given
        Member kai = memberRepository.save(KAI.create(ANYANG_CODE));
        Team kaiTeam = teamRepository.save(HONGDAE_TEAM_1.create(kai, create_3_man()));

        final String objectPath = S3TeamImageService.TEAM_IMAGE_PATH.formatted(kaiTeam.getTeamId());
        setAuthentication(kai.getMemberId(), "MANAGER");

        // when & then
        return List.of(
            // upload Test
            DynamicTest.dynamicTest("팀 사진을 등록할 수 있다.", () -> {
                // given
                MockMultipartFile uploadFile = new MockMultipartFile("file1", "test1.jpg", "image/jpg", "test1".getBytes());

                // when
                teamImageService.uploadTeamImage(kai.getMemberId(), List.of(uploadFile));

                // then
                List<String> findKeys = findKeys(objectPath);
                List<TeamImage> findTeamImages = teamImageRepository.findTeamImagesByTeamId(kaiTeam.getTeamId());

                assertAll(
                    () -> assertThat(findKeys).hasSize(1),
                    () -> assertThat(findKeys.get(0)).contains(objectPath),
                    () -> assertThat(findTeamImages).hasSize(1)
                        .extracting("sequence", "team")
                        .containsOnly(tuple(1, kaiTeam))
                );
            }),
            // upload Test
            DynamicTest.dynamicTest("팀 사진을 수정할 수 있다.", () -> {
                // given
                List<String> keys = findKeys(objectPath);
                final String key = keys.get(0);
                final MockMultipartFile updateFile = new MockMultipartFile("update2", "update2.jpg", "image/jpg", "update".getBytes());

                // when
                teamImageService.updateTeamImage(kai.getMemberId(), List.of(updateFile));

                // then
                List<String> updateKeys = findKeys(objectPath);
                List<TeamImage> findTeamImages = teamImageRepository.findTeamImagesByTeamId(kaiTeam.getTeamId());

                assertAll(
                    () -> assertThat(updateKeys).hasSize(1),
                    () -> assertThat(updateKeys.get(0)).isNotEqualTo(key),
                    () -> assertThat(findTeamImages).hasSize(1)
                        .extracting("sequence", "team")
                        .containsOnly(tuple(1, kaiTeam))
                );
            }),
            // delete Test
            DynamicTest.dynamicTest("S3에 업로드한 오브젝트를 삭제한다.", () -> {
                // when
                teamImageService.deleteTeamImage(List.of(objectPath));

                // then
                List<String> keys = findKeys(objectPath);
                List<TeamImage> findImages = teamImageRepository.findTeamImagesByTeamId(kaiTeam.getTeamId());
                assertThat(keys).isEmpty();
            })
        );
    }

    private List<String> findKeys(String objectPath) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
            .bucket(teamBucket)
            .prefix(objectPath)
            .build();
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        return listObjectsV2Response.contents()
            .stream()
            .map(S3Object::key)
            .toList();
    }

}
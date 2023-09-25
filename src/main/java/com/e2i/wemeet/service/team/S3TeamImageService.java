package com.e2i.wemeet.service.team;

import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team_image.TeamImage;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.exception.badrequest.ImageCountExceedException;
import com.e2i.wemeet.exception.notfound.TeamNotFoundException;
import com.e2i.wemeet.security.manager.IsManager;
import com.e2i.wemeet.service.aws.s3.S3Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class S3TeamImageService implements TeamImageService {

    private final TeamImageRepository teamImageRepository;
    private final S3Service s3Service;

    public static final String TEAM_IMAGE_PATH = "v1/%d/";
    public static final String TEAM_IMAGE_KEY = TEAM_IMAGE_PATH + "%d/%s.jpg";
    public static final int MAX_IMAGE_COUNT = 10;

    @Value("${aws.s3.teamImageBucket}")
    private String teamImageBucket;

    @Value("${aws.cloudFront.teamImageDomain}")
    private String teamImageDomain;

    /*
     * 팀 이미지 업로드
     * - 새로운 이미지 업로드 (기존 이미지 순서 뒤에 위치)
     * */
    @Transactional
    @IsManager
    @Override
    public List<String> uploadTeamImage(final Long memberId, final List<MultipartFile> images) {
        Team team = teamImageRepository.findTeamByMemberId(memberId)
            .orElseThrow(TeamNotFoundException::new)
            .checkTeamValid();
        final int imageCount = teamImageRepository.countByTeamTeamId(team.getTeamId());

        if (imageCount + images.size() > MAX_IMAGE_COUNT) {
            throw new ImageCountExceedException();
        }

        // 이미지 업로드 & TeamImage 테이블에 이미지 정보 저장
        int startIndex = imageCount + 1;
        return uploadImagesAndSaveTeamImage(images, team, startIndex);
    }

    /*
     * 팀 이미지 수정
     * - 기존 이미지들 삭제 후, 새로운 이미지 추가
     * */
    @Transactional
    @IsManager
    @Override
    public List<String> updateTeamImage(final Long memberId, final List<MultipartFile> images) {
        Team team = teamImageRepository.findTeamByMemberId(memberId)
            .orElseThrow(TeamNotFoundException::new)
            .checkTeamValid();

        // 기존 이미지 삭제
        final String teamImageUrlPath = String.format(TEAM_IMAGE_PATH, team.getTeamId());
        s3Service.delete(teamImageBucket, teamImageUrlPath);
        teamImageRepository.deleteAllByTeamTeamId(team.getTeamId());

        // 이미지 업로드 & TeamImage 테이블에 이미지 정보 저장
        return uploadImagesAndSaveTeamImage(images, team, 1);
    }

    /*
     * 팀 이미지 삭제
     * - 지정된 URL에 해당하는 이미지 삭제
     * */
    @Transactional
    @IsManager
    @Override
    public void deleteTeamImage(final List<String> imageUrls) {
        // 이미지 삭제
        imageUrls.stream()
            .map(imageUrl -> imageUrl.replace(teamImageDomain, ""))
            .forEach(imageUrl -> s3Service.delete(teamImageBucket, imageUrl));
        teamImageRepository.deleteAllByTeamImageUrl(imageUrls);
    }

    // S3에 이미지 업로드 & TeamImage 테이블에 이미지 정보 저장
    private List<String> uploadImagesAndSaveTeamImage(final List<MultipartFile> images, final Team team, int startIndex) {
        List<String> uploadedImageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            final String randomKey = UUID.randomUUID().toString();
            final String imagePath = String.format(TEAM_IMAGE_KEY, team.getTeamId(), startIndex, randomKey);
            s3Service.upload(image, imagePath, teamImageBucket);

            final String imageUrl = teamImageDomain + imagePath;
            teamImageRepository.save(TeamImage.builder()
                .team(team)
                .teamImageUrl(imageUrl)
                .sequence(startIndex)
                .build());
            startIndex++;
            uploadedImageUrls.add(imageUrl);
        }

        return uploadedImageUrls;
    }

}

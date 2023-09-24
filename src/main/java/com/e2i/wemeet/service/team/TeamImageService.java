package com.e2i.wemeet.service.team;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TeamImageService {

    /*
     * 팀 이미지 upload
     * */
    public List<String> uploadTeamImage(Long memberId, List<MultipartFile> images);

    /*
     * 팀 이미지 update
     * */
    public List<String> updateTeamImage(Long memberId, List<MultipartFile> images);

    /*
     * 팀 이미지 delete
     * */
    public void deleteTeamImage(List<String> imageUrls);

}

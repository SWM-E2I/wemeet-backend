package com.e2i.wemeet.controller.team;

import com.e2i.wemeet.config.resolver.member.MemberId;
import com.e2i.wemeet.dto.request.team.DeleteTeamImageRequestDto;
import com.e2i.wemeet.dto.response.ResponseDto;
import com.e2i.wemeet.service.team.TeamImageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/v1/team/image")
@RestController
public class TeamImageController {

    private final TeamImageService teamImageService;

    @PostMapping
    public ResponseDto<List<String>> upload(@MemberId Long memberId, @RequestPart("images") List<MultipartFile> images) {
        List<String> uploadedImageUrls = teamImageService.uploadTeamImage(memberId, images);
        return ResponseDto.success("Team Image Upload Success", uploadedImageUrls);
    }

    @PutMapping
    public ResponseDto<List<String>> update(@MemberId Long memberId, @RequestPart("images") List<MultipartFile> images) {
        List<String> uploadedImageUrls = teamImageService.updateTeamImage(memberId, images);
        return ResponseDto.success("Team Image Update Success", uploadedImageUrls);
    }

    @DeleteMapping
    public ResponseDto<Void> delete(@Valid @RequestBody DeleteTeamImageRequestDto requestDto) {
        teamImageService.deleteTeamImage(requestDto.deleteImageUrls());
        return ResponseDto.success("Delete Team Image Success");
    }

}

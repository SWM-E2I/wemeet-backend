package com.e2i.wemeet.service.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.dto.request.member.CreateMemberRequestDto;
import com.e2i.wemeet.dto.request.member.ModifyMemberRequestDto;
import com.e2i.wemeet.dto.response.member.MemberDetailResponseDto;
import com.e2i.wemeet.dto.response.member.MemberInfoResponseDto;
import com.e2i.wemeet.dto.response.member.RoleResponseDto;
import com.e2i.wemeet.exception.notfound.MemberNotFoundException;
import com.e2i.wemeet.service.aws.s3.S3Service;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.profileImageBucket}")
    private String profileImageBucket;

    private static final String BASIC_SUFFIX = "-basic";
    private static final String LOW_SUFFIX = "-low";
    private static final String FILE_EXTENSION = ".jpg";


    // TODO :: service refactoring
    @Override
    @Transactional
    public Long createMember(CreateMemberRequestDto requestDto) {

        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional
    public void modifyMember(Long memberId, ModifyMemberRequestDto requestDto) {

    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public MemberDetailResponseDto getMemberDetail(Long memberId) {
        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        return null;
    }

    // TODO :: service refactoring
    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getMemberRole(Long memberId) {
        return null;
    }

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        // member.delete();

    }

    @Override
    @Transactional
    public void uploadProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new)
            .checkMemberValid();

        String objectKey = createObjectKey(memberId);
        s3Service.upload(file, objectKey + BASIC_SUFFIX + FILE_EXTENSION, profileImageBucket);

        member.saveProfileImage(ProfileImage.builder()
            .basicUrl(objectKey + BASIC_SUFFIX + FILE_EXTENSION)
            .lowUrl(objectKey + LOW_SUFFIX + FILE_EXTENSION)
            .build());
    }

    private String createObjectKey(Long memberId) {
        String uuid = UUID.randomUUID().toString();
        return "v1/profileImage/" + memberId + "/" + uuid;
    }
}

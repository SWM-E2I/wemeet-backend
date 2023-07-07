package com.e2i.wemeet.domain.profileimage;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByMemberMemberIdAndIsMain(Long memberId, boolean isMain);

    List<ProfileImage> findByMemberMemberId(Long memberId);

}

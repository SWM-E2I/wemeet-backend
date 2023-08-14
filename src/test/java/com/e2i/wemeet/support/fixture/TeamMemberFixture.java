package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.dto.request.member.CollegeInfoRequestDto;
import com.e2i.wemeet.dto.request.team.TeamMemberRequestDto;
import java.util.List;
import lombok.Getter;

@Getter
public enum TeamMemberFixture {
    // MAN
    TOM(CollegeInfoFixture.KOREA.create(), Mbti.ENFJ),
    WILSON(CollegeInfoFixture.KOREA.create(), Mbti.ENFP),
    BENJAMIN(CollegeInfoFixture.INHA.create(), Mbti.ESFJ),
    LIAM(CollegeInfoFixture.ANYANG.create(), Mbti.ESFP),
    OLIVER(CollegeInfoFixture.INHA.create(), Mbti.INTJ),
    JAMES(CollegeInfoFixture.ANYANG.create(), Mbti.INTP),
    NOAH(CollegeInfoFixture.KOREA.create(), Mbti.INFJ),

    // WOMAN
    OLIVIA(CollegeInfoFixture.WOMAN.create(), Mbti.INFP),
    RACHEL(CollegeInfoFixture.INHA.create(), Mbti.INFJ),
    EMMA(CollegeInfoFixture.WOMAN.create(), Mbti.ENFP),
    ELIJAH(CollegeInfoFixture.ANYANG.create(), Mbti.ENFJ),
    ELLI(CollegeInfoFixture.WOMAN.create(), Mbti.ENTJ),
    ;

    private final CollegeInfo collegeInfo;
    private final Mbti mbti;

    TeamMemberFixture(CollegeInfo collegeInfo, Mbti mbti) {
        this.collegeInfo = collegeInfo;
        this.mbti = mbti;
    }

    public TeamMember create() {
        return TeamMember.builder()
            .collegeInfo(this.collegeInfo)
            .mbti(this.mbti)
            .build();
    }

    public static List<TeamMember> create_3_man() {
        return List.of(
            TOM.create(),
            WILSON.create(),
            BENJAMIN.create()
        );
    }

    public static List<TeamMember> create_3_woman() {
        return List.of(
            OLIVIA.create(),
            RACHEL.create(),
            EMMA.create()
        );
    }

    public static List<TeamMember> create_1_woman() {
        return List.of(
            OLIVIA.create()
        );
    }

    public TeamMemberRequestDto createTeamMemberRequestDto() {
        CollegeInfoRequestDto collegeInfoRequestDto = createCollegeInfoRequestDto();

        return TeamMemberRequestDto.builder()
            .collegeInfo(collegeInfoRequestDto)
            .mbti(this.mbti.name())
            .build();
    }

    private CollegeInfoRequestDto createCollegeInfoRequestDto() {
        return CollegeInfoRequestDto.builder()
            .collegeCode(getCollegeCodeWithDelimiter(this.collegeInfo.getCollegeCode()))
            .collegeType(this.collegeInfo.getCollegeType().name())
            .admissionYear(this.collegeInfo.getAdmissionYear())
            .build();
    }

    private String getCollegeCodeWithDelimiter(Code code) {
        CodePk codePk = code.getCodePk();
        return codePk.getGroupCodeId() + "-" + codePk.getCodeId();
    }
}

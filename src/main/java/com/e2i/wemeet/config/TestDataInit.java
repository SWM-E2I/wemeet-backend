package com.e2i.wemeet.config;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.domain.code.GroupCodeRepository;
import com.e2i.wemeet.domain.heart.Heart;
import com.e2i.wemeet.domain.heart.HeartRepository;
import com.e2i.wemeet.domain.meeting.Meeting;
import com.e2i.wemeet.domain.meeting.MeetingRepository;
import com.e2i.wemeet.domain.meeting.MeetingRequest;
import com.e2i.wemeet.domain.meeting.MeetingRequestRepository;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.MemberRepository;
import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.member.data.ProfileImage;
import com.e2i.wemeet.domain.member.data.Role;
import com.e2i.wemeet.domain.team.Team;
import com.e2i.wemeet.domain.team.TeamRepository;
import com.e2i.wemeet.domain.team.data.AdditionalActivity;
import com.e2i.wemeet.domain.team.data.DrinkRate;
import com.e2i.wemeet.domain.team.data.DrinkWithGame;
import com.e2i.wemeet.domain.team.data.Region;
import com.e2i.wemeet.domain.team_image.TeamImageRepository;
import com.e2i.wemeet.domain.team_member.TeamMember;
import com.e2i.wemeet.domain.team_member.TeamMemberRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class TestDataInit {

    private final Init init;

    @PostConstruct
    public void init() {
        init.init();
    }

    @Transactional
    @RequiredArgsConstructor
    @Component
    static class Init {

        private final GroupCodeRepository groupCodeRepository;
        private final CodeRepository codeRepository;
        private final MemberRepository memberRepository;
        private final TeamRepository teamRepository;
        private final TeamMemberRepository teamMemberRepository;
        private final TeamImageRepository teamImageRepository;
        private final MeetingRequestRepository meetingRequestRepository;
        private final MeetingRepository meetingRepository;
        private final HeartRepository heartRepository;
        private final EntityManager em;

        protected Code KOREA_CODE;
        protected Code ANYANG_CODE;
        protected Code INHA_CODE;
        protected Code WOMANS_CODE;
        protected Code HANYANG_CODE;

        private static final int DATA_COUNT = 1_000;
        private static final int INTERVAL = 100;

        public void init() {
            initCode();

            List<Member> manMembers = createManMembers(1, 1000_0000);
            List<Member> womanMembers = createWomanMembers(100_010, 8123_4560);

            List<Team> manTeam = createTeam(manMembers);
            List<Team> womanTeam = createTeam(womanMembers);

            // 받은 신청
            Team targetTeam = womanTeam.get(0);
            List<MeetingRequest> requestList = new ArrayList<>();
            int index = 0;

            for (int i = index; i < index + INTERVAL; i++) {
                requestList.add(MeetingRequest.builder()
                    .team(manTeam.get(1))
                    .partnerTeam(targetTeam)
                    .message("Hi there! zz ")
                    .build());
            }
            index += INTERVAL;

            // 보낸 신청
            for (int i = index; i < index + INTERVAL; i++) {
                requestList.add(MeetingRequest.builder()
                    .team(targetTeam)
                    .partnerTeam(manTeam.get(i))
                    .message("Hi Daemon Thread ! zz ")
                    .build());
            }
            index += INTERVAL;
            meetingRequestRepository.saveAll(requestList);
            em.flush();

            List<Meeting> meetingList = new ArrayList<>();
            for (int i = index; i < index + INTERVAL; i++) {
                meetingList.add(Meeting.builder()
                    .team(targetTeam)
                    .partnerTeam(manTeam.get(i))
                    .build());
            }
            index += INTERVAL;
            meetingRepository.saveAll(meetingList);
            em.flush();

            // 받은 좋아요
            List<Heart> heartList = new ArrayList<>();
            for (int i = index; i < index + INTERVAL; i++) {
                heartList.add(Heart.builder()
                    .team(manTeam.get(i))
                    .partnerTeam(targetTeam)
                    .build());
            }
            index += INTERVAL;

            // 보낸 좋아요
            for (int i = index; i < index + INTERVAL; i++) {
                heartList.add(Heart.builder()
                    .team(targetTeam)
                    .partnerTeam(manTeam.get(i))
                    .build());
            }
            index += INTERVAL;

            heartRepository.saveAll(heartList);
            em.flush();
        }

        private List<Team> createTeam(List<Member> members) {
            List<Team> saveTeamList = new ArrayList<>();

            for (Member member : members) {
                Team team = Team.builder()
                    .memberNum(3)
                    .region(Region.HONGDAE)
                    .drinkRate(DrinkRate.HIGH)
                    .drinkWithGame(DrinkWithGame.HATER)
                    .additionalActivity(AdditionalActivity.OUTDOOR_ACTIVITY)
                    .introduction("잘 부 탁 !")
                    .chatLink("Impossible7")
                    .teamLeader(member)
                    .build();
                team.addTeamMembers(List.of(
                    TeamMember.builder()
                        .collegeInfo(CollegeInfo.builder()
                            .collegeCode(INHA_CODE)
                            .collegeType(CollegeType.EDUCATION)
                            .admissionYear("20")
                            .build())
                        .mbti(Mbti.ENTP)
                        .team(team)
                        .build(),
                    TeamMember.builder()
                        .collegeInfo(CollegeInfo.builder()
                            .collegeCode(INHA_CODE)
                            .collegeType(CollegeType.EDUCATION)
                            .admissionYear("20")
                            .build())
                        .mbti(Mbti.ENTP)
                        .team(team)
                        .build()
                ));

                saveTeamList.add(team);
            }

            List<Team> teams = teamRepository.saveAll(saveTeamList);
            em.flush();
            return teams;
        }

        private List<Member> createManMembers(int memberCount, int phoneNumber) {
            List<Member> manMemberList = new ArrayList<>();

            for (int i = 0; i < DATA_COUNT; i++) {
                phoneNumber += memberCount;
                String phone = "+8210" + phoneNumber;
                System.out.println(":::phone = " + phone);

                manMemberList.add(Member.builder()
                    .nickname("유니 " + memberCount)
                    .gender(Gender.MAN)
                    .phoneNumber(phone)
                    .email("test" + phoneNumber + "@korea.ac.kr")
                    .credit(100)
                    .collegeInfo(CollegeInfo.builder()
                        .collegeCode(KOREA_CODE)
                        .collegeType(CollegeType.ARTS)
                        .admissionYear("19")
                        .build())
                    .mbti(Mbti.ENFJ)
                    .allowMarketing(false)
                    .profileImage(ProfileImage.builder()
                        .basicUrl("adfl@gag.com")
                        .lowUrl("povwl@gag.com")
                        .imageAuth(true)
                        .build())
                    .role(Role.MANAGER)
                    .build());
                memberCount++;
            }

            List<Member> members = memberRepository.saveAll(manMemberList);
            em.flush();
            return members;
        }

        private List<Member> createWomanMembers(int memberCount, int phoneNumber) {
            List<Member> womanMemberList = new ArrayList<>();

            for (int i = 0; i < DATA_COUNT; i++) {
                phoneNumber += memberCount;

                womanMemberList.add(Member.builder()
                    .nickname("세히 " + memberCount)
                    .gender(Gender.WOMAN)
                    .phoneNumber("+8210" + phoneNumber)
                    .email("woma" + phoneNumber + "@korea.ac.kr")
                    .credit(100)
                    .collegeInfo(CollegeInfo.builder()
                        .collegeCode(KOREA_CODE)
                        .collegeType(CollegeType.ARTS)
                        .admissionYear("19")
                        .build())
                    .mbti(Mbti.ENFJ)
                    .allowMarketing(false)
                    .profileImage(ProfileImage.builder()
                        .basicUrl("adfl@gag.com")
                        .lowUrl("povwl@gag.com")
                        .imageAuth(true)
                        .build())
                    .role(Role.MANAGER)
                    .build());
                memberCount++;
            }

            List<Member> members = memberRepository.saveAll(womanMemberList);
            em.flush();
            return members;
        }

        private void initCode() {
            KOREA_CODE = codeRepository.findByCodeValue("고려대")
                .orElseThrow();
            ANYANG_CODE = codeRepository.findByCodeValue("안양대")
                .orElseThrow();
            INHA_CODE = codeRepository.findByCodeValue("인하대")
                .orElseThrow();
            WOMANS_CODE = codeRepository.findByCodeValue("서울여대")
                .orElseThrow();
            HANYANG_CODE = codeRepository.findByCodeValue("한양대")
                .orElseThrow();
        }
    }
}

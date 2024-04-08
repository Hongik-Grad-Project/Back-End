package trackers.demo.domain.volunteer.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.domain.volunteer.entity.Organization;
import trackers.demo.domain.volunteer.entity.Volunteer;
import trackers.demo.domain.volunteer.repository.OrganizationRepository;
import trackers.demo.domain.volunteer.repository.VolunteerRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {

    private final VolunteerRepository volunteerRepository;
    private final OrganizationRepository organizationRepository;
    private static final int FIRST_PAGE_INDEX = 2;     // 페이지 시작 번호
    private static final int LAST_PAGE_INDEX = 2;      // 페이지 끝 번호

    public void VMScrawl() {

        try {
            for(int i = FIRST_PAGE_INDEX; i <= LAST_PAGE_INDEX; i++){

                // VMS 봉사 신청 페이지
                final String vmsURL =
                        "https://www.vms.or.kr/partspace/recruit.do?area=&areagugun=&acttype=&status=1&volacttype=&" +
                        "sttdte=2024-04-01&enddte=2024-05-01&" +
                        "termgbn=&searchType=title&searchValue=&page=" + i;

                Connection conn = Jsoup.connect(vmsURL);
                Document document = conn.get();

                Elements linkElements = document.select("ul.list_wrap > li > a");

                for(int j = 0; j < linkElements.size(); j++){

                    // 봉사 게시글의 URL
                    String url = linkElements.get(j).attr("abs:href");
                    System.out.println( j+1 + "번째 url 주소: " + url);

                    // 봉사 링크 내부 진입
                    Connection innerConn = Jsoup.connect(url);
                    Document innerDocument = innerConn.get();

                    // 봉사 제목
                    Element titleElement = innerDocument.selectFirst("div.viewTitle > p.tit");
                    String title = Objects.isNull(titleElement) ? "제목 없음" : titleElement.text();
                    System.out.println(j+1 + "번째 봉사 제목 = " + title);

                    Elements groups = innerDocument.select("div.group");    // div.group 요소가 존재하는지 확인
                    if(!groups.isEmpty()){

                        // 활동 기간 (봉사 활동 시작 시간, 봉사 활동 마감 시간)
                        Element group1 = innerDocument.select("div.group").get(0);
                        String period = group1.select("dt:contains(활동기간) + dd").text();

                        if(period.isEmpty()) period = "기간 없음";
                        String[] dates;
                        String startDateString, endDateString;
                        LocalDate startDate, endDate;   // 봉사 활동 시작 기간, 마감 기간
                        if(period.equals("기간 없음")){
                            startDate = null;
                            endDate = null;
                        } else {
                            dates = period.split(" ~ ");
                            startDateString = dates[0];
                            endDateString = dates[1];
                            startDate = LocalDate.parse(startDateString);
                            endDate = LocalDate.parse(endDateString);
                        }
                        System.out.println(j+1 + "번째 봉사 활동 시작 날짜 = " + startDate);
                        System.out.println(j+1 + "번째 봉사 활동 종료 날짜 = " + endDate);


                        // 봉사 활동처
                        Element group2 = innerDocument.select("div.group").get(1);
                        String volunteerOrganization = group2.select("dt:contains(봉사활동처) + dd").text();
                        if(volunteerOrganization.isEmpty() ) volunteerOrganization = "봉사 활동처 없음";
                        System.out.println(j+1 + "번째 봉사 활동처 = " + volunteerOrganization);

                        // 봉사 주기
                        String activityCycle = group2.select("dt:contains(활동주기) + dd").text();
                        if(activityCycle.isEmpty() ) activityCycle = "봉사 주기 없음";
                        System.out.println(j+1 + "번째 봉사 주기 = " + activityCycle);

                        // 봉사 지역
                        Element group3 = innerDocument.select("div.group").get(2);
                        String volunteerArea = group3.select("dt:contains(봉사지역) + dd").text();
                        if(volunteerArea.isEmpty() ) volunteerArea = "봉사 지역 없음";
                        System.out.println(j+1 + "번째 봉사 지역 = " + volunteerArea);

                        // 봉사 장소
                        String location = group3.select("dt:contains(봉사장소) + dd").text();
                        if(location.isEmpty() ) location = "봉사 장소 없음";
                        System.out.println(j+1 + "번째 봉사 장소 = " + location);

                        // 봉사 대상
                        Element group4 = innerDocument.select("div.group").get(3);
                        String target = group4.select("dt:contains(봉사대상) + dd").text();
                        if(target.isEmpty() ) target = "봉사 대상 없음";
                        System.out.println(j+1 + "번째 봉사 대상 = " + target);

                        // 봉사 필요/신청 인원
                        String stringApplicants = group4.select("dt:contains(필요/신청 인원) + dd").text();
                        if(stringApplicants.isEmpty() ) stringApplicants = "봉사 필요/신청 인원 없음";
                        System.out.println(j+1 + "번째 봉사 필요/신청 인원 = " + stringApplicants);

                        String[] parts;
                        int numRequired = 0;
                        int numApplicant = 0;
                        if(!stringApplicants.equals("봉사 필요/신청 인원 없음")){
                            stringApplicants = stringApplicants.trim();
                            parts = stringApplicants.split(" / ");
                            numRequired = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                            numApplicant = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
                        }
                        System.out.println(j+1 + "번째 봉사 필요 인원 = " + numRequired);
                        System.out.println(j+1 + "번째 봉사 신청 인원 = " + numApplicant);

                        // 봉사 활동처 DB 먼저 저장
                        Organization newOrganization;
                        Boolean isExist = organizationRepository.existsByName(volunteerOrganization);
                        if(isExist){
                            newOrganization = organizationRepository.findByName(volunteerOrganization);
                        }else {
                            newOrganization = Organization.builder()
                                    .name(volunteerOrganization)
                                    .build();
                            organizationRepository.save(newOrganization);
                        }

                        // 봉사 DB 저장
                        Volunteer volunteer = Volunteer.builder()
                                .title(title)
                                .startDate(startDate)
                                .endDate(endDate)
                                .organization(newOrganization)
                                .activityCycle(activityCycle)
                                .area(volunteerArea)
                                .location(location)
                                .target(target)
                                .numRequired(numRequired)
                                .numApplicant(numApplicant)
                                .build();
                        volunteerRepository.save(volunteer);
                    }



                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

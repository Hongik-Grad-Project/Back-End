package trackers.demo.domain.volunteer.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trackers.demo.domain.volunteer.entity.Volunteer;
import trackers.demo.domain.volunteer.repository.VolunteerRepository;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CrawlingService {

    private final VolunteerRepository volunteerRepository;
    private static final int FIRST_PAGE_INDEX = 1;     // 페이지 시작 번호
    private static final int LAST_PAGE_INDEX = 10ㅎ;      // 페이지 끝 번호 (지금 마지막 페이지는 290)

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
                    // 첫 번째 봉사 게시글의 URL
                    final String url = linkElements.get(j).attr("abs:href");
//                    System.out.println( j + "번째 url 주소: " + url);

                    // 봉사 링크 내부 진입
                    Connection innerConn = Jsoup.connect(url);
                    Document innerDocument = innerConn.get();

                    // 봉사 제목
                    Element titleElement = innerDocument.selectFirst("div.viewTitle > p.tit");
                    final String title = Objects.isNull(titleElement) ? "제목 없음" : titleElement.text();
//                    System.out.println(j+1 + "번째 봉사 제목 = " + title);

                    // 봉사 기간
                    Element periodElemnet = innerDocument.selectFirst("div.group > dl > dd");
                    final String period = Objects.isNull(periodElemnet) ? "기간 없음" : periodElemnet.text();
//                    System.out.println(j+1 + "번째 봉사의 활동기간 = " + period);

                    // DB에 저장
                    Volunteer volunteer = Volunteer.builder()
                            .title(title)
                            .period(period)
                            .build();
                    volunteerRepository.save(volunteer);

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

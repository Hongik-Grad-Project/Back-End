package trackers.demo.domain.volunteer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.domain.volunteer.service.CrawlingService;
import trackers.demo.domain.volunteer.service.ExcelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crawling")
public class CrawlingController {

    private final CrawlingService crawlingService;
    private final ExcelService excelService;

    @PostMapping("/vms")
    public String crawlVMSData(){

        /* 추후에 관리자만 해당 API를 호출할 수 있도록 수정 */

        crawlingService.VMScrawl();     // DB에 저장

        // 엑셀 파일에 저장
//        excelService.writeToExcel();      // DB 데이터를 엑셀에 저장

        return "크롤링을 성공적으로 수행했습니다.";
    }


}

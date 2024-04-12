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

        /* 관리자만 해당 API를 호출할 수 있도록 수정 */

        crawlingService.VMScrawl();     // DB에 저장
        excelService.writeToExcel();      // DB 데이터를 엑셀에 저장

        return "크롤링을 성공적으로 수행했습니다.";
    }

    @PostMapping("/vms/new")
    public String updateVMSData(){
        // 매일 자정에 해당 API가 호출되도록 설정
        // 1. 마감기한(봉사시작날짜)가 지난 봉사는 DB에서 지우기
        // 2. 새로 추가된 봉사 데이터를 엑셀 파일에 입력
        return "크롤링을 성공적으로 수행했습니다.";
    }


}

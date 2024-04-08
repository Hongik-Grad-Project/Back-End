package trackers.demo.domain.volunteer.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import trackers.demo.domain.volunteer.entity.Volunteer;
import trackers.demo.domain.volunteer.repository.VolunteerRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final VolunteerRepository volunteerRepository;

    public void writeToExcel() {
        final String filePath = "C:/Users/Jemin/OneDrive/바탕 화면/vms.xlsx";
        List<Volunteer> data = volunteerRepository.findAll();

        // 엑셀 파일 생성
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            Sheet sheet = workbook.createSheet("Data");
            int rowNum = 0;
            for (Volunteer volunteer : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(volunteer.getId());
                row.createCell(1).setCellValue(volunteer.getTitle());
                row.createCell(2).setCellValue(volunteer.getStartDate());
                row.createCell(3).setCellValue(volunteer.getEndDate());
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

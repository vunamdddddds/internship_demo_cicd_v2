package com.example.InternShip.service.impl;

import com.example.InternShip.entity.Intern;
import com.example.InternShip.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public ByteArrayInputStream exportInternEvaluations(List<Intern> interns) {
        String[] HEADERS = {
                "ID Intern", "Họ Tên", "Email", "Nhóm",
                "Điểm Chuyên Môn", "Điểm Chất Lượng", "Điểm GQVĐ", "Điểm Học CN Mới",
                "Kỹ Năng Mềm", "Nhận Xét Của Mentor"
        };
        String SHEET_NAME = "BaoCaoDanhGia";

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            //Tạo Header Row
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                cell.setCellStyle(headerCellStyle);
            }

            //Đổ dữ liệu
            int rowIdx = 1;
            for (Intern intern : interns) {
                Row dataRow = sheet.createRow(rowIdx++);

                dataRow.createCell(0).setCellValue(intern.getId());
                dataRow.createCell(1).setCellValue(intern.getUser().getFullName());
                dataRow.createCell(2).setCellValue(intern.getUser().getEmail());
                dataRow.createCell(3).setCellValue(intern.getTeam() != null ? intern.getTeam().getName() : "N/A");

                // Set điểm (xử lý an toàn nếu bị null)
                dataRow.createCell(4).setCellValue(safeToDouble(intern.getExpertiseScore()));
                dataRow.createCell(5).setCellValue(safeToDouble(intern.getQualityScore()));
                dataRow.createCell(6).setCellValue(safeToDouble(intern.getProblemSolvingScore()));
                dataRow.createCell(7).setCellValue(safeToDouble(intern.getTechnologyLearningScore()));

                // Set Kỹ năng mềm (xử lý an toàn nếu bị null)
                dataRow.createCell(8).setCellValue(intern.getSoftSkill() != null ? intern.getSoftSkill().name() : "N/A");
                dataRow.createCell(9).setCellValue(intern.getAssessment() != null ? intern.getAssessment() : "N/A");
            }

            //Tự động điều chỉnh độ rộng cột
            for (int col = 0; col < HEADERS.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

    // Hàm an toàn để chuyển đổi BigDecimal sang Double cho Excel
    private double safeToDouble(BigDecimal decimal) {
        return (decimal != null) ? decimal.doubleValue() : 0.0;
    }
}
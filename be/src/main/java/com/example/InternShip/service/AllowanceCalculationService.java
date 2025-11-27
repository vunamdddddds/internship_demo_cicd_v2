package com.example.InternShip.service;

import com.example.InternShip.dto.cloudinary.response.FileResponse;
import com.example.InternShip.entity.Allowance;
import com.example.InternShip.entity.AllowancePackage;
import com.example.InternShip.entity.Intern;
import com.example.InternShip.entity.InternshipProgram;
import com.example.InternShip.entity.MonthlyAllowanceReport;
import com.example.InternShip.repository.AllowanceRepository;
import com.example.InternShip.repository.AttendanceRepository;
import com.example.InternShip.repository.InternRepository;
import com.example.InternShip.repository.MonthlyAllowanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AllowanceCalculationService {

    @Autowired
    private AllowancePackageService allowancePackageService;
    @Autowired
    private InternRepository internRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AllowanceRepository allowanceRepository;
    @Autowired
    private AllowanceReportService allowanceReportService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private MonthlyAllowanceReportRepository monthlyAllowanceReportRepository;

    @Transactional
    public void calculateMonthlyAllowances(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Intern> activeInterns = internRepository.findAllByStatus(Intern.Status.ACTIVE);
        if (activeInterns.isEmpty()) {
            System.out.println("No active interns found for allowance calculation.");
            return;
        }

        // Lấy số ngày công thực tế của từng intern trong tháng
        Map<Integer, Long> workDaysMap = activeInterns.stream()
                .collect(Collectors.toMap(
                        Intern::getId,
                        intern -> attendanceRepository.countWorkDays(intern.getId(), startDate, endDate)
                ));

        List<Allowance> newlyGeneratedAllowances = new ArrayList<>();

        for (Intern intern : activeInterns) {
            InternshipProgram program = intern.getInternshipProgram();
            if (program == null) {
                continue;
            }
            List<AllowancePackage> activePackages = allowancePackageService.findActiveAllowancePackageEntitiesByProgramId(program.getId());
            if (activePackages.isEmpty()) {
                continue;
            }

            // lấy ra số ngày công thực tế của intern trong tháng 
            long actualWorkDays = workDaysMap.getOrDefault(intern.getId(), 0L);

            for (AllowancePackage pkg : activePackages) {
                if (actualWorkDays >= pkg.getRequiredWorkDays()) {
                    Allowance allowance = new Allowance();
                    allowance.setIntern(intern);
                    allowance.setAllowancePackage(pkg);
                    allowance.setAmount(pkg.getAmount());
                    allowance.setStatus(Allowance.Status.PENDING);
                    allowance.setAllowanceMonth(startDate);
                    allowance.setPeriod(startDate);
                    allowance.setWorkDays((int) actualWorkDays);
                    
                    Allowance savedAllowance = allowanceRepository.save(allowance);
                    newlyGeneratedAllowances.add(savedAllowance);
                }
            }
        }
        
        // After all allowances are calculated and saved, generate and upload the summary report
        if (!newlyGeneratedAllowances.isEmpty()) {
            try {
                // 1. Generate DETAILED report
                byte[] reportBytes = allowanceReportService.generateAllowanceReport(yearMonth);
                
                // 2. Upload to Cloudinary
                String monthString = yearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy"));
                String fileName = "danh_sach_intern_tro_cap_thang_" + monthString + ".xlsx";
                FileResponse fileResponse = cloudinaryService.uploadFile(reportBytes, fileName, "allowance-reports");
                
                // 3. Save report info to DB
                MonthlyAllowanceReport report = new MonthlyAllowanceReport();
                report.setReportMonth(startDate);
                report.setFileName(fileName);
                report.setFileUrl(fileResponse.getFileUrl());
                monthlyAllowanceReportRepository.save(report);

                System.out.println("Successfully generated and uploaded allowance summary report for " + monthString);

            } catch (IOException e) {
                // Log the error but don't fail the entire transaction
                System.err.println("Error generating or uploading allowance summary report for " + yearMonth + ": " + e.getMessage());
            }
        }
    }
}

package com.example.InternShip.config.scheduler;

import com.example.InternShip.service.AllowanceCalculationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
public class MonthlyAllowanceCalculationJob implements Job {

    @Autowired
    private AllowanceCalculationService allowanceCalculationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Starting MonthlyAllowanceCalculationJob...");
        // Calculate allowances for the previous month
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        allowanceCalculationService.calculateMonthlyAllowances(previousMonth);
        System.out.println("MonthlyAllowanceCalculationJob completed for " + previousMonth);
    }
}

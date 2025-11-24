package com.example.InternShip.job;

import com.example.InternShip.service.InternshipProgramService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndPublishJob implements Job {
    @Autowired
    private InternshipProgramService internshipProgramService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int programId = jobExecutionContext.getMergedJobDataMap().getInt("programId");
        internshipProgramService.endPublish(programId);
    }
}

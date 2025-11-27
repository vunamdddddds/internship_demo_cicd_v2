package com.example.InternShip.config;

import com.example.InternShip.config.scheduler.MonthlyAllowanceCalculationJob;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
public class QuartzConfig {

    @Bean
    public JobFactory jobFactory(AutowireCapableBeanFactory beanFactory) {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setBeanFactory(beanFactory);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        factory.setDataSource(dataSource); // 👈 thêm dòng này để Quartz biết dùng datasource của Spring Boot
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws Exception {
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        return scheduler;
    }

    // Bean for the Allowance Calculation Job
    @Bean
    public JobDetail monthlyAllowanceCalculationJobDetail() {
        return JobBuilder.newJob(MonthlyAllowanceCalculationJob.class)
                .withIdentity("monthlyAllowanceCalculationJob")
                .storeDurably()
                .build();
    }

    // Trigger for the Allowance Calculation Job
    @Bean
    public Trigger monthlyAllowanceCalculationJobTrigger() {
        // Run at 00:00 on the 1st day of every month
        // This job will then calculate allowances for the *previous* month
        return TriggerBuilder.newTrigger()
                .forJob(monthlyAllowanceCalculationJobDetail())
                .withIdentity("monthlyAllowanceCalculationTrigger")
                .withSchedule(cronSchedule("0 0 0 1 * ?")) // Cron expression for 00:00 on the 1st day of every month
                .build();
    } 
}

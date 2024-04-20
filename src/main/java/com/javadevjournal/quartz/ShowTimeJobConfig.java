package com.javadevjournal.quartz;

import org.quartz.CronScheduleBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

@Configuration
public class ShowTimeJobConfig {
    @Value("${cron.init.send}")
    private String CRON_INIT_NOTIFICATION_CREATEORDER;

    @Bean
    public JobDetail showTimeJobDetail() {
        return JobBuilder
                .newJob(SendCheckJob.class)
                .withIdentity("SendCheckJob", "PERMANENT")
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger showTimeTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(showTimeJobDetail())
                .withIdentity("SendCheckJob", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_INIT_NOTIFICATION_CREATEORDER))
                .build();
    }
}



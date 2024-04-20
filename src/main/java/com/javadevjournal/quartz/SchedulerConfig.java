package com.javadevjournal.quartz;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;

@Configuration
public class SchedulerConfig {

    @Bean
    public Scheduler scheduler(List<Trigger> triggers, List<JobDetail> jobDetails, SchedulerFactoryBean factory) throws SchedulerException {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        var scheduler = factory.getScheduler();
        revalidateJobs(jobDetails, scheduler);
        rescheduleTriggers(triggers, scheduler);
        scheduler.start();
        return scheduler;
    }

    private void rescheduleTriggers(List<Trigger> triggers, Scheduler scheduler) {
        triggers.forEach(it -> {
            try {
                if (!scheduler.checkExists(it.getKey())) {
                    scheduler.scheduleJob(it);
                } else {
                    scheduler.rescheduleJob(it.getKey(), it);
                }
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void revalidateJobs(List<JobDetail> jobDetails, Scheduler scheduler) throws SchedulerException {
        var jobKeys = jobDetails.stream().map(JobDetail::getKey).toList();
        scheduler.getJobKeys(GroupMatcher.jobGroupEquals("PERMANENT")).forEach(it -> {
            if (!jobKeys.contains(it)) {
                try {
                    scheduler.deleteJob(it);
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}


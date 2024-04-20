package com.javadevjournal.quartz;

import com.javadevjournal.service.AdsService;
import lombok.AllArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
@AllArgsConstructor
public class SendCheckJob extends QuartzJobBean {
   private final AdsService adsService;

    @Override
    public void executeInternal(JobExecutionContext context) {
        adsService.autoSendCheck();
    }
}


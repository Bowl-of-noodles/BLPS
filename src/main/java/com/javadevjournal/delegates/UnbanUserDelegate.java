package com.javadevjournal.delegates;

import com.javadevjournal.quartz.SendCheckJob;
import com.javadevjournal.service.CustomerService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class UnbanUserDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(UnbanUserDelegate.class.getName());
    private final CustomerService customerService;

    public UnbanUserDelegate(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        try {
            customerService.unban();
        } catch (Throwable throwable) {
            throw new BpmnError("unban-error", throwable.getMessage());
        }
    }
}
package com.javadevjournal.delegates;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class ComplaintDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final AdsService adsService;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public ComplaintDelegate(AdsService adsService, JwtUtil jwtUtil, CustomerService customerService) {
        this.adsService = adsService;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            Long customer_id = Long.parseLong((String) delegateExecution.getVariable("id"));

            String username = jwtUtil.usernameFromToken((String) delegateExecution.getVariable("token"));
            Long id = customerService.getUserId(username);
            Customer customer = customerService.findById(id);

            customerService.complaint(customer,customer_id);
            delegateExecution.setVariable("result", "Complaint was successfully created");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Complaint was successfully created");
        } catch (Throwable throwable) {
            throw new BpmnError("complaint-error", throwable.getMessage());
        }
    }
}

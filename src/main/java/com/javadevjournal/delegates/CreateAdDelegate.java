package com.javadevjournal.delegates;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class CreateAdDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final AdsService adsService;
    private final JwtUtil jwtUtil;
    private final JmsTemplate jmsTemplate;
    private final CustomerService customerService;

    public CreateAdDelegate(AdsService adsService, JwtUtil jwtUtil, CustomerService customerService, JmsTemplate jmsTemplate) {
        this.adsService = adsService;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            Long price = Long.parseLong((String) delegateExecution.getVariable("price"));
            Double weight = Double.parseDouble((String)delegateExecution.getVariable("weight"));
            String category = (String) delegateExecution.getVariable("category");
            String phone = (String) delegateExecution.getVariable("phone");
            String description = (String) delegateExecution.getVariable("description");

            String username = jwtUtil.usernameFromToken((String) delegateExecution.getVariable("token"));
            Long id = customerService.getUserId(username);
            String message = adsService.createAd(new AdDTO(price, weight, category, phone, description), id);
            //jmsTemplate.convertAndSend("second-queue", message);

            delegateExecution.setVariable("result", "Ad was successfully created");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Ad was successfully created");
        } catch (Throwable throwable) {
            throw new BpmnError("create-ad-error", throwable.getMessage());
        }
    }
}

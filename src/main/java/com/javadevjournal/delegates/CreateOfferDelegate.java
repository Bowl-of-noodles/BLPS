package com.javadevjournal.delegates;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import com.javadevjournal.service.OfferService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class CreateOfferDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final OfferService offerService;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public CreateOfferDelegate(OfferService offerService, JwtUtil jwtUtil, CustomerService customerService) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            Long ad_id = Long.parseLong((String) delegateExecution.getVariable("ad_id"));

            String username = jwtUtil.usernameFromToken((String) delegateExecution.getVariable("token"));
            Long id = customerService.getUserId(username);
            Customer customer = customerService.findById(id);

            offerService.createOffer(customer, ad_id);
            delegateExecution.setVariable("result", "Offer was successfully created");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Offer was successfully created");
        } catch (Throwable throwable) {
            throw new BpmnError("offer-create-error", throwable.getMessage());
        }
    }
}

package com.javadevjournal.delegates;

import com.javadevjournal.dto.AdDTO;
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
public class AddFavAdDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final AdsService adsService;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public AddFavAdDelegate(AdsService adsService, JwtUtil jwtUtil, CustomerService customerService) {
        this.adsService = adsService;
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

            customerService.addToFav(customer, ad_id);
            delegateExecution.setVariable("result", "Ad was successfully added to favorites");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Ad was successfully added to favorites");
        } catch (Throwable throwable) {
            throw new BpmnError("add-to-fav-ad-error", throwable.getMessage());
        }
    }
}

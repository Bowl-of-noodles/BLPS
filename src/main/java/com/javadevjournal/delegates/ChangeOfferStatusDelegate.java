package com.javadevjournal.delegates;

import com.javadevjournal.dto.StatusDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.enums.RoleName;
import com.javadevjournal.jpa.enums.StatusName;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.CustomerService;
import com.javadevjournal.service.OfferService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class ChangeOfferStatusDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final OfferService offerService;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public ChangeOfferStatusDelegate(OfferService offerService, JwtUtil jwtUtil, CustomerService customerService) {
        this.offerService = offerService;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            Long ad_id = Long.parseLong((String) delegateExecution.getVariable("id"));
            String status = (String) delegateExecution.getVariable("status");


            String username = jwtUtil.usernameFromToken((String) delegateExecution.getVariable("token"));
            Long id = customerService.getUserId(username);
            Customer customer = customerService.findById(id);


            if(!customer.getRole().getName().equals(RoleName.ADMIN)){
                logger.log(Level.WARNING, "403 Forbidden");
                throw new BpmnError("no access", "403 Forbidden");
            }

            offerService.changeOfferStatus(ad_id, new StatusDTO(status));
            delegateExecution.setVariable("result", "Status was successfully changed");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Status was successfully changed");
        } catch (Throwable throwable) {
            throw new BpmnError("offer-status-error", throwable.getMessage());
        }
    }
}

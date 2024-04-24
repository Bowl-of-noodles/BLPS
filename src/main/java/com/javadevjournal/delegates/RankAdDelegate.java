package com.javadevjournal.delegates;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.RankDTO;
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
public class RankAdDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(CreateAdDelegate.class.getName());
    private final AdsService adsService;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;

    public RankAdDelegate(AdsService adsService, JwtUtil jwtUtil, CustomerService customerService) {
        this.adsService = adsService;
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            Long ad_id = Long.parseLong((String) delegateExecution.getVariable("ad_id"));
            Double rank = Double.parseDouble((String)delegateExecution.getVariable("rank"));


            //String username = jwtUtil.usernameFromToken((String) delegateExecution.getVariable("token"));
            //Long customer_id = customerService.getUserId(username);

            adsService.rank(ad_id, new RankDTO(rank));
            delegateExecution.setVariable("result", "Ad was successfully ranked");
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "Ad was successfully ranked");
        } catch (Throwable throwable) {
            throw new BpmnError("rank-ad-error", throwable.getMessage());
        }
    }
}

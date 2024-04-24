package com.javadevjournal.delegates;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AuthorizationService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Named
public class RegisterDelegate implements JavaDelegate {
    private static final Logger logger = Logger.getLogger(RegisterDelegate.class.getName());
    private final JwtUtil jwtUtil;
    private final AuthorizationService authService;

    public RegisterDelegate(JwtUtil jwtUtil, AuthorizationService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        try {
            String username = (String) delegateExecution.getVariable("username");
            String password = (String) delegateExecution.getVariable("password");
            Customer newUser = authService.registerUser(new CustomerDTO(username, password));
            String token = jwtUtil.generateToken(newUser.getUserName());
            delegateExecution.setVariable("token", token);
            logger.log(Level.INFO, "Current activity is " + delegateExecution.getCurrentActivityName());
            logger.log(Level.INFO, "New user signed up with username=" + newUser.getUserName() + " and id=" + newUser.getId());
        } catch (Throwable throwable) {
            throw new BpmnError("register_error", throwable.getMessage());
        }
    }
}

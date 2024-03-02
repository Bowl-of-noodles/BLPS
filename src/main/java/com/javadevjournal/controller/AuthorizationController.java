package com.javadevjournal.controller;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthorizationController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> authUser(@RequestBody CustomerDTO loginRequestDTO) {
        Map<Object, Object> model = new HashMap<>();
        Customer authUser = authorizationService.authUser(loginRequestDTO);
        model.put("token", jwtUtil.generateToken(authUser.getUserName()));
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CustomerDTO registerRequestDTO){
        Map<Object, Object> model = new HashMap<>();
        Customer newUser = authorizationService.registerUser(registerRequestDTO);
        model.put("token", jwtUtil.generateToken(newUser.getUserName()));
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}

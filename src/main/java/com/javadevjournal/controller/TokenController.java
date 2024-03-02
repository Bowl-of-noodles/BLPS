package com.javadevjournal.controller;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TokenController {

    private final CustomerService customerService;

    /*@PostMapping("/token")
    //public String getToken(@RequestParam("username") final String username, @RequestParam("password") final String password) {
    public String getToken(@RequestBody CustomerDTO customerDTO) {
        String token = customerService.login(customerDTO);
        System.out.println(token);
        if (StringUtils.isEmpty(token)) {
            return "no token found";
        }
        String jsonString = "{\"token\": \"" + token + "\"}";
        return jsonString;
    }

    @PostMapping(value = "/registration")
    public Customer createNewUser(@RequestBody CustomerDTO customerDTO) {
        return customerService.registration(customerDTO);
    }*/

    @GetMapping(value = "/all", produces = "application/json")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }
}

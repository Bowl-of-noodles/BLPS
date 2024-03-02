package com.javadevjournal.service;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.enums.RoleName;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.jpa.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("authService")
public class AuthorizationService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Customer authUser(CustomerDTO loginRequestDTO) throws NoAuthorityException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getName(), loginRequestDTO.getPassword()));
        if (!authentication.isAuthenticated()) {
            throw new NoAuthorityException("Ошибка авторизации");
        }
        Optional<Customer> user = customerRepository.findByUserName(loginRequestDTO.getName());
        if (user == null) {
            //throw new NoSuchUserException("Пользователя с таким логином не существует");
        }
        return user.get();
    }

    public Customer registerUser(CustomerDTO registerRequestDTO)  {
       /* if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new InvalidDataException("Пользователь с таким email уже существует");
        }*/
        Customer customer = new Customer();
        customer.setUserName(registerRequestDTO.getName());
        customer.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        customer.setRole(roleRepository.findByName(RoleName.USER));

        var res = customerRepository.findByUserNameAndPassword(
                registerRequestDTO.getName(),
                registerRequestDTO.getPassword()
        );

        return res.orElseGet(() -> customerRepository.save(customer));
    }

}

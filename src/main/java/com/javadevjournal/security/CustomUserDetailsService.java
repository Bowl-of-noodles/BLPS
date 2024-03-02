package com.javadevjournal.security;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username){
        Optional<Customer> customer = customerRepository.findByUserName(username);
        //if (user == null) throw new UsernameNotFoundException("User with username - " + username + " not found");
        return CustomUserDetails.build(customer.get());
    }
}

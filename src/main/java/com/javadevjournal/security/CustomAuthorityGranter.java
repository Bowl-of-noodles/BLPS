package com.javadevjournal.security;

import com.javadevjournal.jpa.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.jaas.AuthorityGranter;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class CustomAuthorityGranter implements AuthorityGranter {
    private final CustomerRepository customerRepository;
    @Override
    public Set<String> grant(Principal principal) {
        String role = String.valueOf(customerRepository.findRoleByName(principal.getName()).getName());
        return Collections.singleton(role);
    }
}

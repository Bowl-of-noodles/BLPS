package com.javadevjournal.service;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

	//String login(String username, String password);
	String login(CustomerDTO customerDTO);

	List<Customer> findAll();

	Optional<User> findByToken(String token);

	Customer findById(Long id);

	Customer registration(CustomerDTO customerDTO);

	Optional<Customer> whoIs(HttpServletRequest httpServletRequest);

	void deleteMe(HttpServletRequest httpServletRequest);

	String complaint(HttpServletRequest httpServletRequest, Long customerId);

	void save(Customer customer);
}

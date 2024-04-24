package com.javadevjournal.service;

import com.javadevjournal.dto.FullCustomerDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CustomerService {


	List<Customer> findAll();

	String findByToken(String token);

	Customer findById(Long id);

	void unban();

	Optional<Customer> whoIs(HttpServletRequest httpServletRequest);
	Long getUserId(String username);

	void deleteMe(HttpServletRequest httpServletRequest);

	MessageDTO addToFav(Customer customer, Long id);
	MessageDTO delFromFav(Customer customer, Long id);

	List<Ad> showFav(HttpServletRequest httpServletRequest);

	String complaint(Customer customer, Long customerId);

	FullCustomerDTO customerInfo(Customer customer);

	void save(Customer customer);
}

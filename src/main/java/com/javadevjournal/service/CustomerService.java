package com.javadevjournal.service;

import com.javadevjournal.dto.FullCustomerDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.FavAdRow;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface CustomerService {


	List<Customer> findAll();

	String findByToken(String token);

	Customer findById(Long id);

	Optional<Customer> whoIs(HttpServletRequest httpServletRequest);
	Optional<Customer> whoIsAdmin(HttpServletRequest httpServletRequest);

	void deleteMe(HttpServletRequest httpServletRequest);

	MessageDTO addToFav(HttpServletRequest httpServletRequest, Long id);
	MessageDTO delFromFav(HttpServletRequest httpServletRequest, Long id);

	List<Ad> showFav(HttpServletRequest httpServletRequest);

	String complaint(HttpServletRequest httpServletRequest, Long customerId);

	FullCustomerDTO customerInfo(Customer customer);

	void save(Customer customer);
}

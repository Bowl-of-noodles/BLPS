package com.javadevjournal.service;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;

import java.util.List;

public interface OfferService {

	void save(Offer offer);

	void delete(Offer offer);

	List<Offer> findAllByCustomer(Customer customer);
}

package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends CrudRepository<Offer, Long> {

	List<Offer> findAllByCustomer(Customer customer);
}
package com.javadevjournal.service;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.repository.OfferRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service("offerService")
public class OfferServiceImpl implements OfferService {

	@Autowired
	private OfferRepository offerRepository;

	@Override
	public void save(Offer offer) {
		offerRepository.save(offer);
	}

	@Override
	public void delete(Offer offer) {
		offerRepository.delete(offer);
	}

	@Override
	public List<Offer> findAllByCustomer(Customer customer) {
		return offerRepository.findAllByCustomer(customer);
	}

}

package com.javadevjournal.service;

import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.dto.OfferDTO;
import com.javadevjournal.dto.StatusDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;

import java.util.List;

public interface OfferService {

	void save(Offer offer);

	void delete(Offer offer);

	List<OfferDTO> findAllOffers();

	OfferDTO getOffer(Long id);

	OfferDTO createOffer(Customer customer, Long adId);

	List<OfferDTO> findAllByCustomer(Customer customer);

	List<OfferDTO> findAllByStatus(StatusDTO statusDTO);

	MessageDTO changeOfferStatus(Long id, StatusDTO statusDTO);
}

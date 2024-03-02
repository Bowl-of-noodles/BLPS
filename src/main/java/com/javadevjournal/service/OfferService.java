package com.javadevjournal.service;

import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.dto.OfferDTO;
import com.javadevjournal.dto.StatusDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.enums.StatusName;

import java.util.List;
import java.util.Optional;

public interface OfferService {

	void save(Offer offer);

	void delete(Offer offer);

	OfferDTO getOffer(Long id);

	OfferDTO createOffer(Customer customer, Long adId);

	List<OfferDTO> findAllByCustomer(Customer customer);

	List<OfferDTO> findAllByStatus(StatusDTO statusDTO);

	MessageDTO changeOfferStatus(Long id, StatusDTO statusDTO);
}

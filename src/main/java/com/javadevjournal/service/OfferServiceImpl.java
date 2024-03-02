package com.javadevjournal.service;

import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.dto.OfferDTO;
import com.javadevjournal.dto.StatusDTO;
import com.javadevjournal.exceptions.EmptyEnterException;
import com.javadevjournal.exceptions.NotFoundException;
import com.javadevjournal.exceptions.WrongInputException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.enums.StatusName;
import com.javadevjournal.jpa.repository.OfferRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Service("offerService")
public class OfferServiceImpl implements OfferService {

	@Autowired
	private OfferRepository offerRepository;
	private final AdsServiceImpl adsService;

	@Override
	@Transactional
	public void save(Offer offer) {
		offerRepository.save(offer);
	}

	@Override
	@Transactional
	public void delete(Offer offer) {
		offerRepository.delete(offer);
	}

	@Override
	@Transactional
	public List<OfferDTO> findAllByCustomer(Customer customer) {
		List<Offer> offers= offerRepository.findAllByCustomer(customer);
		List<OfferDTO> offerDTOS = new ArrayList<>();
		for (Offer offer : offers) {
			offerDTOS.add(getOfferDTO(offer));
		}

		return offerDTOS;
	}

	@Override
	@Transactional
	public List<OfferDTO> findAllByStatus(StatusDTO statusDTO) {
		if(statusDTO.getStatus().equals("")){
			throw new EmptyEnterException("Вы не ввели значение статуса");
		}
		if(Arrays.stream(StatusName.values()).noneMatch(e -> e.name().equals(statusDTO.getStatus()))){
			throw new WrongInputException("Нет такого варианта статуса. Неправильный ввод");
		}
		List<Offer> offers= new ArrayList<>();
		String inputStatus = statusDTO.getStatus().toUpperCase();
		for (StatusName statusName : StatusName.values()) {
			if (statusName.equals(StatusName.valueOf(inputStatus))) {
				offers = offerRepository.findAllByStatus(statusName);
			}
		}

		List<OfferDTO> offerDTOS = new ArrayList<>();
		for (Offer offer : offers) {
			offerDTOS.add(getOfferDTO(offer));
		}


		return offerDTOS;
	}

	@Override
	@Transactional
	public OfferDTO createOffer(Customer customer, Long adId){
		Ad ad = adsService.getById(adId);

		Offer offer = new Offer();
		offer.setCustomer(customer);
		offer.setCreationDate(LocalDate.now());
		offer.setAd(ad);
		offer.setPrice(ad.getPrice());
		offer.setStatus(StatusName.PROCESSING);
		offerRepository.save(offer);

		return getOfferDTO(offer);

	}

	private OfferDTO getOfferDTO(Offer offer){
		OfferDTO offerDTO = new OfferDTO();
		offerDTO.setId(offer.getId());
		offerDTO.setCreationDate(offer.getCreationDate());
		offerDTO.setCustomerId(offer.getCustomer().getId());
		offerDTO.setAdId(offer.getAd().getId());
		offerDTO.setPrice(offer.getPrice());
		offerDTO.setStatus(offer.getStatus());
		return offerDTO;
	}

	@Override
	@Transactional
	public MessageDTO changeOfferStatus(Long id, StatusDTO statusDTO){
		if(statusDTO.getStatus().equals("")){
			throw new EmptyEnterException("Вы не ввели значение статуса");
		}
		MessageDTO messageDTO = new MessageDTO();
		Optional<Offer> offerOpt = offerRepository.findById(id);
		Offer offer = offerOpt.get();

		String status = statusDTO.getStatus().toUpperCase();
		if(Arrays.stream(StatusName.values()).noneMatch(e -> e.name().equals(status))){
			throw new WrongInputException("Нет такого варианта статуса. Неправильный ввод");
		}
		offer.setStatus(StatusName.valueOf(status));
		offerRepository.save(offer);

		messageDTO.setMessage("Статус заказа изменен на: " + statusDTO.getStatus());
		return messageDTO;
	}

	@Override
	@Transactional
	public OfferDTO getOffer(Long id){
		Optional<Offer> offer = offerRepository.findById(id);
		return getOfferDTO(offer.get());
	}
}

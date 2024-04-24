package com.javadevjournal.service;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.dto.RankDTO;
import com.javadevjournal.jpa.entity.Ad;

import java.util.List;

public interface AdsService {

	List<Ad> findAdsByFilter(Long minPrice, Long maxPrice, Double weight, String category);

	List<Ad> findMyAds(Long id);

	List<Ad> findAllAds();

	void deleteAllByOwner(Long id);

	String createAd(AdDTO adDTO, Long id);

	void save(Ad ad);

	MessageDTO rank(Long id, RankDTO rankDTO);

	Ad getById(Long id);
	MessageDTO changeAd(AdDTO adDTO, Long id);

	void sendAd(Long id);

	void autoSendCheck();

}

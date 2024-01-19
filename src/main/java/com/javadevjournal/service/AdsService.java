package com.javadevjournal.service;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.RankDTO;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;

import java.util.List;

public interface AdsService {

	List<Ad> findAdsByFilter(Long minPrice, Long maxPrice, Double weight, String category);

	List<Ad> findMyAds(Long id);

	void deleteAllByOwner(Long id);

	Ad createAd(AdDTO adDTO, Long id);

	void save(Ad ad);

	String rank(Long id, RankDTO rankDTO);

	Ad getById(Long id);

}

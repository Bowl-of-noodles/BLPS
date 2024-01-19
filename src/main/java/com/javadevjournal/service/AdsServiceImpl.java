package com.javadevjournal.service;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.RankDTO;
import com.javadevjournal.exceptions.NotFoundException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Offer;
import com.javadevjournal.jpa.repository.AdsRepository;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service("adsService")
public class AdsServiceImpl implements AdsService {

	private final OfferService offerService;

	@Autowired
	private AdsRepository adsRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public List<Ad> findAdsByFilter(Long minPrice, Long maxPrice, Double weight, String category) {
		return adsRepository.findAllByPriceBetweenAndWeightAndCategory(minPrice, maxPrice, weight, category);
	}

	@Override
	public List<Ad> findMyAds(Long id) {
		return adsRepository.findAllByOwner(id);
	}


	@Override
	public void deleteAllByOwner(Long id) {
		adsRepository.deleteAllByOwner(id);
	}

	@Override
	public Ad createAd(AdDTO adDTO, Long id) {
		Ad ad = new Ad();
		ad.setOwner(id);
		ad.setPrice(adDTO.getPrice());
		ad.setWeight(adDTO.getWeight());
		ad.setPhone(adDTO.getPhone());
		ad.setCategory(adDTO.getCategory());

		return adsRepository.save(ad);
	}

	@Override
	public Ad getById(Long id) {
		Optional<Ad> ad = adsRepository.findById(id);
		return ad.orElseThrow(() -> new MyResourceNotFoundException("Пользователь не найден"));
	}

	@Override
	public void save(Ad ad) {
		adsRepository.save(ad);
	}

	@Override
	@Transactional
	public String rank(Long id, RankDTO rankDTO){
		Optional<Ad> adOpt;
		try{
			adOpt = adsRepository.findById(id);
		}
		catch(Exception exception){
			throw new NotFoundException("Нет такого объявления");
		}

		if (adOpt.isEmpty()) {
			throw new MyResourceNotFoundException("вы не авторизованы");
		}
		var ad = adOpt.get();
		var mark = rankDTO.getRank();
		return rank(ad, mark);
	}


	private String rank(Ad ad, Double mark) {
		if ((ad.getRank() != null) && (ad.getRank()> 0)) {
			double rank = ad.getRank();
			rank = (rank + mark) / 2;
			ad.setRank(rank);
		} else {
			ad.setRank(mark);
		}
		return "Объявление успешно обновлено";
	}


}

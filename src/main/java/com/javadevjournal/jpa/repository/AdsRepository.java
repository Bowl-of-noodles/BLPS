package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.enums.AdStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdsRepository extends CrudRepository<Ad, Long> {

	List<Ad> findAllByPriceBetweenAndWeightAndCategory(Long minPrice,
													   Long maxPrice,
													   Double weight,
													   String category);

	List<Ad> findAllByOwner(Long owner);

	List<Ad> findAllByStatus(AdStatus status);

	void deleteAllByOwner(Long customer);

	Optional<Ad> findFirstByStatus(AdStatus status);

}
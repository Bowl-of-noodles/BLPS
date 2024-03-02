package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Repository
public interface AdsRepository extends CrudRepository<Ad, Long> {

	List<Ad> findAllByPriceBetweenAndWeightAndCategory(Long minPrice,
													   Long maxPrice,
													   Double weight,
													   String category);

	List<Ad> findAllByOwner(Long owner);

	void deleteAllByOwner(Long customer);

}
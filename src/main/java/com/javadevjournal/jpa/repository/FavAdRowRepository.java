package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.FavAdRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavAdRowRepository extends JpaRepository<FavAdRow, Long> {

    List<FavAdRow> findByCustomer(Customer customer);

}

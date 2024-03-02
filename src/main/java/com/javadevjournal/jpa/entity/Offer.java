package com.javadevjournal.jpa.entity;

import com.javadevjournal.jpa.enums.StatusName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Offer {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name = "offer_id")
	private Long id;

	@Column(name = "creationDate")
	private LocalDate creationDate;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Ad ad;

	@Column(name = "price")
	private Long price;

	@Column(name = "status")
	private StatusName status;
}

package com.javadevjournal.jpa.entity;

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

@Entity
@Getter
@Setter
@ToString
public class Offer {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name = "offer_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "offered_price")
	private Long price;

	@Column(name = "cancelled")
	private boolean cancelled;
}

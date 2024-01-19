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
public class Ad {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ad_id")
	private Long id;

	/*@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer owner;*/

	@Column(name = "customer_id")
	private Long owner;

	@Column(name = "price")
	private Long price;

	@Column(name = "weight")
	private Double weight;

	@Column(name = "category")
	private String category;

	@Column(name = "phone")
	private String phone;

	@Column(name = "rank")
	private Double rank;

}

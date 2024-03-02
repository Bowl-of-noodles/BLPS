package com.javadevjournal.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

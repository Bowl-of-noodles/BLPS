package com.javadevjournal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdDTO {

	private Long price;
	private Double weight;
	private String category;
	private String phone;
	private String description;

}

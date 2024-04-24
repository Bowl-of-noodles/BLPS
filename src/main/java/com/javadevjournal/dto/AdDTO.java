package com.javadevjournal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AdDTO {

	private Long price;
	private Double weight;
	private String category;
	private String phone;
	private String description;

}

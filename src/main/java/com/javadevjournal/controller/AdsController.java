package com.javadevjournal.controller;

import com.javadevjournal.dto.UsernameDTO;
import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@AllArgsConstructor
@RequestMapping("/api/ads")
public class AdsController {


	private final CustomerService customerService;

	@GetMapping(value = "/whoIs", produces = MediaType.APPLICATION_JSON_VALUE)
	public UsernameDTO whoIs(HttpServletRequest httpServletRequest) {
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		System.out.println("Token: " + token);
		token = StringUtils.removeStart(token, "Bearer").trim();
		String name = customerService.findByToken(token);
        return new UsernameDTO(name);
	}
}

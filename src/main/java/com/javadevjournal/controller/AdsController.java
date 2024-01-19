package com.javadevjournal.controller;

import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
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

	@GetMapping(value = "/whoIs")
	public String whoIs(HttpServletRequest httpServletRequest) {
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		String name = customerService.findByToken(token).get().getUsername();
		//return String.format("Пользователь : %s", name);
		String jsonString = "{\"user\": \"" + name + "\"}";
		//JSONObject username = new JSONObject(jsonString);
		//System.out.println(username);
		return jsonString;
	}
}

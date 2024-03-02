package com.javadevjournal.service;

import com.javadevjournal.dto.FullCustomerDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.FavAdRow;
import com.javadevjournal.jpa.enums.RoleName;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private final AdsService adsService;
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private JwtUtil jwtUtil;


	@Override
	@Transactional
	public List<Customer> findAll() {
		return (List<Customer>) customerRepository.findAll();
	}

	@Override
	public String findByToken(String token) {

		String username = jwtUtil.usernameFromToken(token);

		return username;
	}

	@Override
	@Transactional
	public Customer findById(Long id) {
		Optional<Customer> customer = customerRepository.findById(id);
		return customer.orElseThrow(() -> new MyResourceNotFoundException("Пользователь не найден"));
	}

	@Override
	public FullCustomerDTO customerInfo(Customer customer){
		FullCustomerDTO customerDTO = new FullCustomerDTO();
		customerDTO.setId(customer.getId());
		customerDTO.setUserName(customer.getUserName());
		customerDTO.setPassword(customer.getPassword());
		customerDTO.setRole(customer.getRole().getName());
		customerDTO.setKarmaNegative(customer.getKarmaNegative());
		customerDTO.setBanned(customer.isBanned());
		return customerDTO;
	}

	@Override
	@Transactional
	public Optional<Customer> whoIs(HttpServletRequest httpServletRequest){
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		String username = findByToken(token);
		return customerRepository.findByUserName(username);
	}

	@Override
	public Optional<Customer> whoIsAdmin(HttpServletRequest httpServletRequest){
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		String username = findByToken(token);
		Optional<Customer> custOpt = customerRepository.findByUserName(username);
		if(custOpt.get().getRole().getName() != RoleName.ADMIN){
			throw new NoAuthorityException("У вас нет прав");
		}
		return custOpt;
	}

	@Override
	@Transactional
	public MessageDTO addToFav(HttpServletRequest httpServletRequest, Long id) {
		MessageDTO message = new MessageDTO();
		var customerOpt = whoIs(httpServletRequest);

		Ad ad = adsService.getById(id);
		var customer = customerOpt.get();
		if(!ad.getOwner().equals(customerOpt.get().getId())){
			var favRow = new FavAdRow();
			favRow.setCustomer(customer);
			favRow.setAd(ad);

			customer.getFavAds().add(favRow);

			message.setMessage("Объявление добавлено в избранное");
			return message;
		}

		message.setMessage("Владелец не может добавить свое объявление в избранное");
		return message;


	}

	@Override
	@Transactional
	public MessageDTO delFromFav(HttpServletRequest httpServletRequest, Long id) {
		var customerOpt = whoIs(httpServletRequest);

		Ad ad = adsService.getById(id);
		var customer = customerOpt.get();
		int index = -1;

		for(int i = 0; i < customer.getFavAds().size(); i++){
			if(customer.getFavAds().get(i).getAd().equals(ad)){
				index = i;
				break;
			}
		}

		customer.getFavAds().remove(index);

		MessageDTO message = new MessageDTO();
		message.setMessage("Объявление убрано из избранного");
		return message;
	}

	@Override
	@Transactional
	public List<Ad> showFav(HttpServletRequest httpServletRequest) {
		var customerOpt = whoIs(httpServletRequest);
		if (customerOpt.isEmpty()) {
			throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
		}
		var customer = customerOpt.get();
		if (customer.isBanned()) {
			throw new MyResourceNotFoundException("Вы забанены, у вас не может быть объявлений");
		}

		List<Ad> myList =new ArrayList<>();
		for(int i = 0; i < customer.getFavAds().size(); i++){
			myList.add(customer.getFavAds().get(i).getAd());
		}/// переписать
		return myList;
	}


	@Override
	@Transactional
	public void deleteMe(HttpServletRequest httpServletRequest) {
		var customerOpt = whoIs(httpServletRequest);
		if (customerOpt.isEmpty()) {
			throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
		}
		var customer = customerOpt.get();
		adsService.deleteAllByOwner(customer.getId());
//		apartmentService.unApprove(customer);
		customerRepository.delete(customer);
	}

	@Override
	@Transactional
	public String complaint(HttpServletRequest httpServletRequest, Long customerId) {
		var customerOpt = whoIs(httpServletRequest);
		if (customerOpt.isEmpty()) {
			throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
		}
		var customer = customerOpt.get();
		if (customer.isBanned()) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь уже забанен");
		}
		Customer anotherUser = findById(customerId);
		if (anotherUser == null) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь не существует");
		}
		if (anotherUser.isBanned()) {
			throw new MyResourceNotFoundException("Пользователя на которого вы жалуетесь уже забанен");
		}
		return complaint(anotherUser);
	}

	@Override
	public void save(Customer customer) {
		customerRepository.save(customer);
	}

	private String complaint(Customer customer) {
		customer.incNegative();
		if (customer.getKarmaNegative() >= 5) {
			customer.setBanned(true);
			customerRepository.save(customer);
			adsService.deleteAllByOwner(customer.getId());
			String status = "Пользователь успешно забанен!";
			String jsonString = "{\"Статус\": \"" + status + "\"}";
			return jsonString;
		} else {
			customerRepository.save(customer);
			String status = "Жалоба успешно отправлена!";
			String jsonString = "{\"Статус\": \"" + status + "\"}";
			return jsonString;
		}
	}
}

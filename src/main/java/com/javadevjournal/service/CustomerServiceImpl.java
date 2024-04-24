package com.javadevjournal.service;

import com.javadevjournal.delegates.UnbanUserDelegate;
import com.javadevjournal.dto.FullCustomerDTO;
import com.javadevjournal.dto.MessageDTO;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.FavAdRow;
import com.javadevjournal.jpa.enums.AdStatus;
import com.javadevjournal.jpa.enums.RoleName;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.messaging.AdMessage;
import com.javadevjournal.security.JwtUtil;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private final AdsService adsService;
	@Autowired
	private CustomerRepository customerRepository;

	private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());

	@Autowired
	private JwtUtil jwtUtil;


	@Override
	@Transactional
	public List<Customer> findAll() {
		//Page<Customer> pageCustomers = customerRepository.findAll(Sort.by("id"));
		//List<Customer> customers = pageCustomers.getContent();
		return (List<Customer>) customerRepository.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
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
	@Transactional(isolation = Isolation.REPEATABLE_READ)
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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Optional<Customer> whoIs(HttpServletRequest httpServletRequest){
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		String username = findByToken(token);
		return customerRepository.findByUserName(username);
	}

	@Override
	public Long getUserId(String username){
		Optional<Customer> userOpt = customerRepository.findByUserName(username);
		Customer user = userOpt.get();
		return user.getId();
	}


	@Override
	@Transactional
	public MessageDTO addToFav(Customer customer, Long id) {
		MessageDTO message = new MessageDTO();


		Ad ad = adsService.getById(id);
		if(!ad.getOwner().equals(customer.getId())){
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
	public MessageDTO delFromFav(Customer customer, Long id) {

		Ad ad = adsService.getById(id);
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
	public String complaint(Customer customer, Long customerId) {
		if (customer.isBanned()) {
			throw new MyResourceNotFoundException("Вы забанены");
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


	@Transactional
	@Override
	public void unban(){
		customerRepository.findAllByBanned(true).parallelStream()
				.filter(c -> c.getBanTime().isBefore(LocalDateTime.now().minusSeconds(300)))
				.forEach(customer -> {
					customer.setBanned(false);
					customer.setKarmaNegative(0);
					customerRepository.save(customer);
					logger.log(Level.INFO, "User with id "+ customer.getId() + " was unbanned");
				});
	}

	@Override
	public void save(Customer customer) {
		customerRepository.save(customer);
	}

	private String complaint(Customer customer) {
		customer.incNegative();
		if (customer.getKarmaNegative() >= 5) {
			customer.setBanned(true);
			customer.setBanTime(LocalDateTime.now());
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

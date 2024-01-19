package com.javadevjournal.service;

import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.repository.CustomerRepository;
import com.javadevjournal.security.MyResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	private final AdsService adsService;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public String login(CustomerDTO customerDTO) {
		Optional<Customer> customer = customerRepository.login(customerDTO.getName(), customerDTO.getPassword());
		if (customer.isPresent()) {
			String token = UUID.randomUUID().toString();
			Customer custom = customer.get();
			custom.setToken(token);
			customerRepository.save(custom);
			return token;
		}

		return StringUtils.EMPTY;
	}

	@Override
	public List<Customer> findAll() {
		return (List<Customer>) customerRepository.findAll();
	}

	@Override
	public Optional<User> findByToken(String token) {
		Optional<Customer> customer = customerRepository.findByToken(token);
		if (customer.isPresent()) {
			Customer customer1 = customer.get();
			User user = new User(customer1.getUserName(), customer1.getPassword(), true, true, true, true,
					AuthorityUtils.createAuthorityList("USER"));
			return Optional.of(user);
		}
		return Optional.empty();
	}

	@Override
	public Customer findById(Long id) {
		Optional<Customer> customer = customerRepository.findById(id);
		return customer.orElseThrow(() -> new MyResourceNotFoundException("Пользователь не найден"));
	}

	@Override
	public Customer registration(CustomerDTO customerDTO) {
		Customer customer = new Customer();
		if (customerDTO.getName() == null || customerDTO.getPassword() == null) {
			throw new MyResourceNotFoundException("Вы ошиблись, отсутствует имя или пароль");
		}
		customer.setUserName(customerDTO.getName());
		customer.setPassword(customerDTO.getPassword());
		var res = customerRepository.findByUserNameAndPassword(
				customerDTO.getName(),
				customerDTO.getPassword()
		);
		return res.orElseGet(() -> customerRepository.save(customer));
	}

	@Override
	public Optional<Customer> whoIs(HttpServletRequest httpServletRequest) {
		String token = StringUtils.isNotEmpty(httpServletRequest.getHeader(AUTHORIZATION)) ?
				httpServletRequest.getHeader(AUTHORIZATION) : "";
		token = StringUtils.removeStart(token, "Bearer").trim();
		return customerRepository.findByToken(token);
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

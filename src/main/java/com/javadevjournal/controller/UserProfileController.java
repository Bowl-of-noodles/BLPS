package com.javadevjournal.controller;

import com.javadevjournal.dto.AdDTO;
import com.javadevjournal.dto.CustomerDTO;
import com.javadevjournal.dto.RankDTO;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.MyResourceNotFoundException;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileController {

    private final CustomerService customerService;
    private final AdsService adsService;

    @GetMapping(value = "/users/user/{id}", produces = "application/json")
    public Customer getUserDetail(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @GetMapping(value = "/ads/ad/{id}", produces = "application/json")
    public Ad getAdDetail(@PathVariable Long id) {
        return adsService.getById(id);
    }

    @DeleteMapping(value = "/users/delete/me")
    public String deleteMe(HttpServletRequest httpServletRequest) {
        customerService.deleteMe(httpServletRequest);
        String status = "Ваш профиль и объявления удалены";
        String jsonString = "{\"Статус\": \"" + status + "\"}";
        return jsonString;
    }

    @GetMapping(value = "/users/user/all", produces = "application/json")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping(value = "/users/complaint/{id}")
    public String complaint(HttpServletRequest httpServletRequest,
                            @PathVariable Long id) {
        return customerService.complaint(httpServletRequest, id);
    }

    @GetMapping(value = "/ads/filter", produces = "application/json")
    public List<Ad> findApartments(@RequestParam("minPrice") final Long minPrice,
                                   @RequestParam("maxPrice") final Long maxPrice,
                                   @RequestParam("weight") final Double weight,
                                   @RequestParam("category") final String category) {
        return adsService.findAdsByFilter(minPrice, maxPrice, weight, category);
    }

    @GetMapping(value = "/ads/my", produces = "application/json")
    public List<Ad> findMyApartments(HttpServletRequest httpServletRequest) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException("Вы забанены, у вас не может быть объявлений");
        }
        Long id = customerOpt.get().getId();
        return adsService.findMyAds(id);
    }


    @PostMapping(value = "/ads/create", produces = "application/json")
    public Ad createApartment(HttpServletRequest httpServletRequest,
                              @RequestBody AdDTO adDTO) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);
        Long id = customerOpt.get().getId();
        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException("Вы забанены, вам нельзя выставлять квартиры на продажу");
        }
        return adsService.createAd(adDTO, id);
    }

    @GetMapping(value = "/ads/ad/{id}/rank", produces = "application/json")
    //public String rankAd(@PathVariable Long id,
    //                     @RequestParam("rank") final Double rank) {
    public String getToken(@PathVariable Long id, @RequestBody RankDTO rankDTO) {
        adsService.rank(id, rankDTO);
        String status = "Объявление оценено";
        String jsonString = "{\"Статус\": \"" + status + "\"}";
        return jsonString;
    }
}
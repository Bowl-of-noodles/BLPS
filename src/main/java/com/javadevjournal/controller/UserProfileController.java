package com.javadevjournal.controller;

import com.javadevjournal.dto.*;
import com.javadevjournal.exceptions.EmptyEnterException;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.exceptions.NotFoundException;
import com.javadevjournal.exceptions.WrongInputException;
import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.security.MyResourceNotFoundException;
import com.javadevjournal.service.AdsService;
import com.javadevjournal.service.CustomerService;
import com.javadevjournal.service.OfferService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileController {

    private final CustomerService customerService;
    private final AdsService adsService;
    private final OfferService offerService;

    @GetMapping(value = "/users/{id}", produces = "application/json")
    public FullCustomerDTO getUserDetail(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        FullCustomerDTO customerInfo = customerService.customerInfo(customer);
        return customerInfo;
    }

    @GetMapping(value = "/ads/{id}", produces = "application/json")
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

    @GetMapping(value = "/users/all", produces = "application/json")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @GetMapping(value = "/ads/all", produces = "application/json")
    public List<Ad> getAllAds() {
        return adsService.findAllAds();
    }

    @GetMapping(value = "/offers/all", produces = "application/json")
    public List<OfferDTO> getAllOffers() {
        return offerService.findAllOffers();
    }

    @GetMapping(value = "/users/complaint/{id}")
    public String complaint(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return customerService.complaint(httpServletRequest, id);
    }

    @GetMapping(value = "/ads/filter", produces = "application/json")
    public List<Ad> findAds(@RequestParam("minPrice") final Long minPrice, @RequestParam("maxPrice") final Long maxPrice, @RequestParam("weight") final Double weight, @RequestParam("category") final String category) {
        return adsService.findAdsByFilter(minPrice, maxPrice, weight, category);
    }

    @GetMapping(value = "/ads/my", produces = "application/json")
    public List<Ad> findMyAds(HttpServletRequest httpServletRequest) {
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
    public Ad createAd(HttpServletRequest httpServletRequest, @RequestBody AdDTO adDTO) {
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

    @GetMapping(value = "/ads/{id}/rank", produces = "application/json")
    //public String rankAd(@PathVariable Long id,
    //                     @RequestParam("rank") final Double rank) {
    public MessageDTO rankAd(@PathVariable Long id, @RequestBody RankDTO rankDTO) {
        MessageDTO messageDTO = adsService.rank(id, rankDTO);
        return messageDTO;
    }

    @GetMapping(value = "/ads/{id}/fav/add", produces = "application/json")
    public MessageDTO addFav(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        MessageDTO message = customerService.addToFav(httpServletRequest, id);
        return message;
    }

    @DeleteMapping (value = "/ads/{id}/fav/del", produces = "application/json")
    public MessageDTO delFav(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        MessageDTO message = customerService.delFromFav(httpServletRequest, id);
        return message;
    }

    @GetMapping (value = "/ads/fav/my", produces = "application/json")
    public List<Ad> showMyFav(HttpServletRequest httpServletRequest) {
        return customerService.showFav(httpServletRequest);
    }


    @PostMapping(value = "/offers/create", produces = "application/json")
    public OfferDTO createOffer(HttpServletRequest httpServletRequest, @RequestBody IdDTO idDTO) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);

        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        if (customer.isBanned()) {
            throw new MyResourceNotFoundException("Вы забанены, вам нельзя выставлять квартиры на продажу");
        }
        return offerService.createOffer(customer, idDTO.getId());
    }

    @GetMapping (value = "/offers/my", produces = "application/json")
    public List<OfferDTO> allMyOffers(HttpServletRequest httpServletRequest) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);

        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        return offerService.findAllByCustomer(customer);
    }

    @PutMapping (value = "/offers/admin/{id}/status", produces = "application/json")
    public MessageDTO changeOfferStatus(HttpServletRequest httpServletRequest, @PathVariable Long id, @RequestBody StatusDTO statusDTO) throws NoAuthorityException {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);

        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        return offerService.changeOfferStatus(id, statusDTO);
    }

    @GetMapping (value = "/offers/{id}", produces = "application/json")
    public OfferDTO getOffer(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);

        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }

        return offerService.getOffer(id);
    }

    @GetMapping (value = "/offers/admin/status", produces = "application/json")
    public List<OfferDTO> getOffersByStatus(HttpServletRequest httpServletRequest, @RequestBody StatusDTO statusDTO)throws NoAuthorityException {
        var customerOpt = customerService.whoIs(httpServletRequest);
        System.out.println(customerOpt);

        if (customerOpt.isEmpty()) {
            throw new MyResourceNotFoundException("Хз как так вышло, вы не авторизованы");
        }
        var customer = customerOpt.get();
        return offerService.findAllByStatus(statusDTO);
    }




    @ExceptionHandler(NotFoundException.class)
    public ExceptionDTO handleNotFoundException(NotFoundException e) {
        return new ExceptionDTO(404, e.getMessage());
    }

    @ExceptionHandler(WrongInputException.class)
    public ExceptionDTO handleWrongInputException(WrongInputException e) {
        return new ExceptionDTO(400, e.getMessage());
    }

    @ExceptionHandler(EmptyEnterException.class)
    public ExceptionDTO handleEmptyInputException(EmptyEnterException e) {
        return new ExceptionDTO(400, e.getMessage());
    }

    @ExceptionHandler(NoAuthorityException.class)
    public ExceptionDTO handleNoAthorityException(NoAuthorityException e) {
        return new ExceptionDTO(403, "У вас нет нужных прав для этой команды");
    }


}
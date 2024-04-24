package com.javadevjournal.jpa.repository;

import com.javadevjournal.jpa.entity.Ad;
import com.javadevjournal.jpa.entity.Customer;
import com.javadevjournal.jpa.entity.Role;
import com.javadevjournal.jpa.enums.RoleName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Query(value = "SELECT u FROM Customer u where u.userName = ?1 and u.password = ?2 ")
    Optional<Customer> login(String username,String password);

    //Optional<Customer> findByToken(String token);

    Optional<Customer> findByUserNameAndPassword(String userName, String password);

    //Customer findByUserName(String userName);


    Optional<Customer> findByUserName(String name);

    List<Customer> findCustomerById(Long id);

    List<Customer> findAllByBanned(Boolean isBanned);

    List<Customer> findAllByRoleName(RoleName role);

    default Role findRoleByName(String userName) {
        Optional<Customer> cust = findByUserName(userName);
        Customer customer = cust.get();
        return customer.getRole();
    }


}

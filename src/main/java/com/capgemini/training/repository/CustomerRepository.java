package com.capgemini.training.repository;


import com.capgemini.training.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);

    List<Customer> findByAddress_CityIgnoreCase(String city);

    List<Customer> findByAddress_StateIgnoreCase(String state);
}
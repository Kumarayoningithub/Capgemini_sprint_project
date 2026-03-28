package com.capgemini.training.controller;

import com.capgemini.training.dto.*;
import com.capgemini.training.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

   
    @GetMapping
    public List<CustomerDTO> getAll() {
        return service.getAllCustomers();
    }

    
    @GetMapping("/{id}")
    public CustomerDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

   
    @GetMapping("/name/{first}/{last}")
    public List<CustomerDTO> getByName(@PathVariable String first,
                                      @PathVariable String last) {
        return service.getByName(first, last);
    }

   
    @GetMapping("/by_city/{city}")
    public List<CustomerDTO> getByCity(@PathVariable String city) {
        return service.getByCity(city);
    }

    
    @GetMapping("/by_state/{state}")
    public List<CustomerDTO> getByState(@PathVariable String state) {
        return service.getByState(state);
    }

   
    @GetMapping("/transactions/{customerId}")
    public List<TransactionDTO> getTransactions(@PathVariable int customerId) {
        return service.getTransactions(customerId);
    }

 
    @GetMapping("/no-transactions")
    public List<CustomerDTO> getNoTransactions() {
        return service.getCustomersWithoutTransactions();
    }

    
    @GetMapping("/pets/{customerId}")
    public List<PetDTO> getPets(@PathVariable int customerId) {
        return service.getCustomerPets(customerId);
    }

   
    @PostMapping("/add")
    public CustomerDTO add(@RequestBody CustomerDTO dto) {
        return service.add(dto);
    }

    
    @PutMapping("/update/{id}")
    public CustomerDTO update(@PathVariable int id,
                              @RequestBody CustomerDTO dto) {
        return service.update(id, dto);
    }
}
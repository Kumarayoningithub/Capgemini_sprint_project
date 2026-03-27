package com.capgemini.training.service;


import com.capgemini.training.entity.Customer;

import com.capgemini.training.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    public List<Customer> getAll() {
        return repo.findAll();
    }

    public Customer getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public Customer add(Customer c) {
        return repo.save(c);
    }

    public Customer update(int id, Customer updated) {
        Customer c = getById(id);
        if (c != null) {
            c.setFirstName(updated.getFirstName());
            c.setLastName(updated.getLastName());
            c.setEmail(updated.getEmail());
            c.setPhoneNumber(updated.getPhoneNumber());
            return repo.save(c);
        }
        return null;
    }
}
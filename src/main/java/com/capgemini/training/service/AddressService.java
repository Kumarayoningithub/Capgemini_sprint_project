package com.capgemini.training.service;


import com.capgemini.training.entity.Address;
import com.capgemini.training.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repo;

    public List<Address> getAll() {
        return repo.findAll();
    }

    public Address getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public Address add(Address address) {
        return repo.save(address);
    }

    public Address update(int id, Address updated) {
        Address a = getById(id);
        if (a != null) {
            a.setStreet(updated.getStreet());
            a.setCity(updated.getCity());
            a.setState(updated.getState());
            a.setZipCode(updated.getZipCode());
            return repo.save(a);
        }
        return null;
    }
}

package com.capgemini.training.service;

import com.capgemini.training.entity.Supplier;
import com.capgemini.training.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository repo;

 
    public List<Supplier> getAll() {
        return repo.findAll();
    }

    
    public Supplier getById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

   
    public Supplier add(Supplier s) {
        return repo.save(s);
    }

   
    public Supplier update(int id, Supplier updated) {
        Supplier s = getById(id);

        s.setName(updated.getName());
        s.setContactPerson(updated.getContactPerson());
        s.setPhoneNumber(updated.getPhoneNumber());
        s.setEmail(updated.getEmail());
        s.setAddress(updated.getAddress());

        return repo.save(s);
    }

   
    public List<Supplier> getByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

   
    public List<Supplier> getByCity(String city) {
        return repo.findByAddress_CityIgnoreCase(city);
    }

  
    public List<Supplier> getByState(String state) {
        return repo.findByAddress_StateIgnoreCase(state);
    }
   
}
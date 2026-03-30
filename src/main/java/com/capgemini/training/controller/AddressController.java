package com.capgemini.training.controller;
import com.capgemini.training.entity.Address;
import com.capgemini.training.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService service;

   
    @GetMapping
    public List<Address> getAll() {
        return service.getAll();
    }

   
    @GetMapping("/{id}")
    public Address getById(@PathVariable int id) {
        return service.getById(id);
    }

   
    @PostMapping("/add")
    public Address add(@RequestBody Address address) {
        return service.add(address);
    }

   
    @PutMapping("/update/{id}")
    public Address update(@PathVariable int id, @RequestBody Address address) {
        return service.update(id, address);
    }
}
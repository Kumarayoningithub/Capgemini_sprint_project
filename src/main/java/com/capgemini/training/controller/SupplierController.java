package com.capgemini.training.controller;

import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.service.SupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @GetMapping
    public List<SupplierDTO> getAll() {
        return service.getAllDTO();
    }

    @GetMapping("/name/{name}")
    public List<SupplierDTO> getByName(@PathVariable String name) {
        return service.getByNameDTO(name);
    }

    @GetMapping("/city/{city}")
    public List<SupplierDTO> getByCity(@PathVariable String city) {
        return service.getByCityDTO(city);
    }

    @GetMapping("/state/{state}")
    public List<SupplierDTO> getByState(@PathVariable String state) {
        return service.getByStateDTO(state);
    }
    
    @GetMapping("/street/{street}")
    public List<SupplierDTO> getByStreet(@PathVariable String street) {
        return service.getByStreetDTO(street);
    }
    
    @GetMapping("/zipcode/{zipcode}")
    public List<SupplierDTO> getByZipcode(@PathVariable String zipcode) {
        return service.getByZipcodeDTO(zipcode);
    }
    

    @GetMapping("/{id}")
    public SupplierDTO getById(@PathVariable int id) {
        return service.getByIdDTO(id);
    }

    @PostMapping("/add")
    public SupplierDTO add(@RequestBody SupplierDTO dto) {
        return service.add(dto);
    }

    @PutMapping("/update/{id}")
    public SupplierDTO update(@PathVariable int id,
                             @RequestBody SupplierDTO dto) {
        return service.update(id, dto);
    }
}
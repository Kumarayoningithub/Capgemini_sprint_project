package com.capgemini.training.controller;

import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetService service;

    @GetMapping
    public List<PetDTO> getPets(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        return service.getPetsFiltered(minPrice, maxPrice, minAge, maxAge);
    }

    @GetMapping("/{petId}")
    public PetDTO getById(@PathVariable int petId) {
        return service.getById(petId);
    }

    @PostMapping("/add")
    public PetDTO add(@RequestBody PetDTO dto) {
        return service.addPet(dto);
    }

    @PutMapping("/update/{petId}")
    public PetDTO update(@PathVariable int petId, @RequestBody PetDTO dto) {
        return service.updatePet(petId, dto);
    }

    @GetMapping("/food_info/{petId}")
    public List<PetFoodDTO> getFoods(@PathVariable int petId) {
        return service.getPetFoods(petId);
    }

    @GetMapping("/suppliers/{petId}")
    public List<SupplierDTO> getSuppliers(@PathVariable int petId) {
        return service.getSuppliersDTO(petId);
    }

    

}
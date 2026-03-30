package com.capgemini.training.controller;

import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.service.PetFoodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/pet_foods")
public class PetFoodController {

    @Autowired
    private PetFoodService service;

    @GetMapping
    public List<PetFoodDTO> getAll(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity) {
        return service.getAllFiltered(minPrice, maxPrice, minQuantity, maxQuantity);
    }

    @GetMapping("/search")
    public List<PetFoodDTO> searchByName(@RequestParam String name) {
        return service.searchByNameDTO(name);
    }

    @GetMapping("/food_type/{type}")
    public List<PetFoodDTO> getByType(@PathVariable String type) {
        return service.getByTypeDTO(type);
    }

    @GetMapping("/brand/{brand}")
    public List<PetFoodDTO> getByBrand(@PathVariable String brand) {
        return service.getByBrandDTO(brand);
    }

    @GetMapping("/{id}")
    public PetFoodDTO getById(@PathVariable int id) {
        return service.getByIdDTO(id);
    }

    @PostMapping("/add")
    public PetFoodDTO add(@RequestBody PetFoodDTO dto) {
        return service.add(dto);
    }

    @PutMapping("/update/{id}")
    public PetFoodDTO update(@PathVariable int id, @RequestBody PetFoodDTO dto) {
        return service.update(id, dto);
    }

    @PutMapping("/quantity/{id}")
    public PetFoodDTO updateQuantity(@PathVariable int id,
                                     @RequestParam int quantity) {
        return service.updateQuantity(id, quantity);
    }
}
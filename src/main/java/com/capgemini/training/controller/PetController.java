package com.capgemini.training.controller;

import com.capgemini.training.dto.*;
import com.capgemini.training.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")  
public class PetController {

    @Autowired
    private PetService service;


    @GetMapping
    public List<PetDTO> getAllPets() {
        return service.getAllPets();
    }

 
    @GetMapping("/{petId}")
    public PetDTO getById(@PathVariable int petId) {
        return service.getPetById(petId);
    }


    @GetMapping("/category/{categoryId}")
    public List<PetDTO> getByCategory(@PathVariable int categoryId) {
        return service.getPetsByCategory(categoryId);
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
    public List<PetSupplierDTO> getSuppliers(@PathVariable int petId) {
        return service.getSuppliersDTO(petId);
    }

    
    @GetMapping("/transaction_history/{petId}")
    public List<TransactionDTO> getTransactions(@PathVariable int petId) {
        return service.getTransactionHistory(petId);
    }

  
    @PostMapping("/{petId}/foods/{foodId}")
    public PetDTO assignFood(@PathVariable int petId, @PathVariable int foodId) {
        return service.assignFood(petId, foodId);
    }

  
    @GetMapping("/{petId}/foods")
    public List<PetFoodDTO> getPetFoods(@PathVariable int petId) {
        return service.getPetFoods(petId);
    }

  
    @PostMapping("/{petId}/suppliers/{supplierId}")
    public PetDTO assignSupplier(@PathVariable int petId, @PathVariable int supplierId) {
        return service.assignSupplier(petId, supplierId);
    }
}
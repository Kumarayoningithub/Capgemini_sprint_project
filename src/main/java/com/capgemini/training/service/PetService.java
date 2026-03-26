package com.capgemini.training.service;

import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetCategoryRepository categoryRepository;

    @Autowired
    private PetFoodRepository foodRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(int id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    public List<Pet> getPetsByCategory(int categoryId) {
        return petRepository.findByCategoryCategoryId(categoryId);
    }

    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet updatePet(int id, Pet updatedPet) {
        Pet pet = getPetById(id);

        pet.setName(updatedPet.getName());
        pet.setBreed(updatedPet.getBreed());
        pet.setAge(updatedPet.getAge());
        pet.setPrice(updatedPet.getPrice());
        pet.setDescription(updatedPet.getDescription());
        pet.setImageUrl(updatedPet.getImageUrl());
        pet.setCategory(updatedPet.getCategory());

        return petRepository.save(pet);
    }

    public List<PetFood> getPetFoods(int petId) {
        return getPetById(petId).getFoods();
    }

    public List<Supplier> getSuppliers(int petId) {
        return getPetById(petId).getSuppliers();
    }

    public List<Transaction> getTransactionHistory(int petId) {
        return transactionRepository.findAll()
                .stream()
                .filter(t -> t.getPet().getPetId() == petId)
                .toList();
    }

    public Pet assignFood(int petId, int foodId) {
        Pet pet = getPetById(petId);
        PetFood food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        pet.getFoods().add(food);
        return petRepository.save(pet);
    }

  
    public Pet assignSupplier(int petId, int supplierId) {
        Pet pet = getPetById(petId);
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (!pet.getSuppliers().contains(supplier)) {
            pet.getSuppliers().add(supplier);
        }

        return petRepository.save(pet);
    }
}
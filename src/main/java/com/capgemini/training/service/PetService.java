package com.capgemini.training.service;

import com.capgemini.training.dto.*;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public PetDTO getById(int petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        return convertToDTO(pet);
    }

    public List<PetDTO> getPetsFiltered(BigDecimal minPrice, BigDecimal maxPrice,
                                        Integer minAge, Integer maxAge) {
        return petRepository.findAll().stream()
                .filter(p -> minPrice == null || p.getPrice() == null
                        || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice() == null
                        || p.getPrice().compareTo(maxPrice) <= 0)
                .filter(p -> minAge == null || p.getAge() >= minAge)
                .filter(p -> maxAge == null || p.getAge() <= maxAge)
                .map(this::convertToDTO)
                .toList();
    }

    public PetDTO addPet(PetDTO dto) {

        Pet pet = new Pet();

        pet.setName(dto.getName());
        pet.setBreed(dto.getBreed());
        pet.setAge(dto.getAge());
        pet.setPrice(dto.getPrice());
        pet.setDescription(dto.getDescription());
        pet.setImageUrl(dto.getImageUrl());

        PetCategory category = resolveCategory(dto);
        if (category != null) {
            pet.setCategory(category);
        }

        Pet saved = petRepository.save(pet);

        return convertToDTO(saved);
    }

    public PetDTO updatePet(int id, PetDTO dto) {

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        pet.setName(dto.getName());
        pet.setBreed(dto.getBreed());
        pet.setAge(dto.getAge());
        pet.setPrice(dto.getPrice());
        pet.setDescription(dto.getDescription());
        pet.setImageUrl(dto.getImageUrl());

        PetCategory category = resolveCategory(dto);
        if (category != null) {
            pet.setCategory(category);
        }

        Pet updated = petRepository.save(pet);

        return convertToDTO(updated);
    }

    public List<PetFoodDTO> getPetFoods(int petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        return pet.getFoods()
                .stream()
                .map(this::convertFoodToDTO)
                .toList();
    }

    public List<SupplierDTO> getSuppliersDTO(int petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        return pet.getSuppliers().stream().map(s -> {
            SupplierDTO dto = new SupplierDTO();

            dto.setSupplierId(s.getSupplierId());
            dto.setName(s.getName());
            dto.setContactPerson(s.getContactPerson());
            dto.setPhoneNumber(s.getPhoneNumber());
            dto.setEmail(s.getEmail());

            if (s.getAddress() != null) {
                dto.setAddressId(s.getAddress().getAddressId());
                dto.setCity(s.getAddress().getCity());
                dto.setState(s.getAddress().getState());

                SupplierDTO.AddressRef addressRef = new SupplierDTO.AddressRef();
                addressRef.setAddressId(s.getAddress().getAddressId());
                dto.setAddress(addressRef);
            }

            return dto;
        }).toList();
    }

    public List<TransactionDTO> getTransactionHistory(int petId) {

        return transactionRepository.findAll()
                .stream()
                .filter(t -> t.getPet().getPetId() == petId)
                .map(this::convertTransactionToDTO)
                .toList();
    }
    
    public PetDTO assignFood(int petId, int foodId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        PetFood food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        boolean alreadyAssigned = pet.getFoods().stream()
                .anyMatch(f -> f.getFoodId() == foodId);

        if (alreadyAssigned) {
            throw new RuntimeException("Food already assigned to this pet");
        }

        pet.getFoods().add(food);

        return convertToDTO(petRepository.save(pet));
    }

    public PetDTO assignSupplier(int petId, int supplierId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        boolean alreadyAssigned = pet.getSuppliers().stream()
                .anyMatch(s -> s.getSupplierId() == supplierId);

        if (alreadyAssigned) {
            throw new RuntimeException("Supplier already assigned to this pet");
        }

        pet.getSuppliers().add(supplier);

        return convertToDTO(petRepository.save(pet));
    }

    private PetDTO convertToDTO(Pet pet) {

        PetDTO dto = new PetDTO();

        dto.setPetId(pet.getPetId());
        dto.setName(pet.getName());
        dto.setBreed(pet.getBreed());
        dto.setAge(pet.getAge());
        dto.setPrice(pet.getPrice());
        dto.setDescription(pet.getDescription());
        dto.setImageUrl(pet.getImageUrl());

        if (pet.getCategory() != null) {
            dto.setCategoryId(pet.getCategory().getCategoryId());
            dto.setCategoryName(pet.getCategory().getName());
        }

        return dto;
    }

    private PetFoodDTO convertFoodToDTO(PetFood food) {

        PetFoodDTO dto = new PetFoodDTO();

        dto.setFoodId(food.getFoodId());
        dto.setName(food.getName());
        dto.setBrand(food.getBrand());
        dto.setType(food.getType());
        dto.setQuantity(food.getQuantity());
        dto.setPrice(food.getPrice());

        return dto;
    }

    private TransactionDTO convertTransactionToDTO(Transaction t) {

        TransactionDTO dto = new TransactionDTO();

        dto.setTransactionId(t.getTransactionId());
        dto.setAmount(t.getAmount());
        dto.setStatus(t.getTransactionStatus().name());
        dto.setTransactionDate(t.getTransactionDate().toString());

        return dto;
    }

    private PetCategory resolveCategory(PetDTO dto) {
        Integer categoryId = dto.getCategoryId();

        if (categoryId == null && dto.getCategory() != null) {
            categoryId = dto.getCategory().getCategoryId();
        }

        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            PetCategory category = categoryRepository.findByName(dto.getCategoryName());

            if (category == null) {
                throw new RuntimeException("Category not found");
            }

            return category;
        }

        return null;
    }
}
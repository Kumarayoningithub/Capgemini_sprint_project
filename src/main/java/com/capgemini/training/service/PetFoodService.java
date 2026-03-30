package com.capgemini.training.service;

import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.entity.PetFood;
import com.capgemini.training.repository.PetFoodRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PetFoodService {

    @Autowired
    private PetFoodRepository repo;

  
    private PetFoodDTO convertToDTO(PetFood f) {
        PetFoodDTO dto = new PetFoodDTO();
        dto.setFoodId(f.getFoodId());
        dto.setName(f.getName());
        dto.setBrand(f.getBrand());
        dto.setType(f.getType());
        dto.setQuantity(f.getQuantity());
        dto.setPrice(f.getPrice());
        return dto;
    }


    private PetFood convertToEntity(PetFoodDTO dto) {
        PetFood f = new PetFood();
        f.setName(dto.getName());
        f.setBrand(dto.getBrand());
        f.setType(dto.getType());
        f.setQuantity(dto.getQuantity());
        f.setPrice(dto.getPrice());
        return f;
    }


    public List<PetFoodDTO> getAllFiltered(BigDecimal minPrice, BigDecimal maxPrice,
                                           Integer minQuantity, Integer maxQuantity) {
        return repo.findAll().stream()
                .filter(f -> minPrice == null || f.getPrice() == null
                        || f.getPrice().compareTo(minPrice) >= 0)
                .filter(f -> maxPrice == null || f.getPrice() == null
                        || f.getPrice().compareTo(maxPrice) <= 0)
                .filter(f -> minQuantity == null || f.getQuantity() >= minQuantity)
                .filter(f -> maxQuantity == null || f.getQuantity() <= maxQuantity)
                .map(this::convertToDTO)
                .toList();
    }


    public PetFoodDTO getByIdDTO(int id) {
        PetFood f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        return convertToDTO(f);
    }


    public PetFoodDTO add(PetFoodDTO dto) {
        PetFood saved = repo.save(convertToEntity(dto));
        return convertToDTO(saved);
    }


    public PetFoodDTO update(int id, PetFoodDTO dto) {
        PetFood f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        f.setName(dto.getName());
        f.setBrand(dto.getBrand());
        f.setType(dto.getType());
        f.setQuantity(dto.getQuantity());
        f.setPrice(dto.getPrice());

        return convertToDTO(repo.save(f));
    }

   
    public PetFoodDTO updateQuantity(int id, int quantity) {
        PetFood f = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        f.setQuantity(quantity);

        return convertToDTO(repo.save(f));
    }


    public List<PetFoodDTO> getByTypeDTO(String type) {
        return repo.findByType(type).stream()
                .map(this::convertToDTO)
                .toList();
    }


    public List<PetFoodDTO> getByBrandDTO(String brand) {
        return repo.findByBrand(brand).stream()
                .map(this::convertToDTO)
                .toList();
    }


    public List<PetFoodDTO> searchByNameDTO(String name) {
        return repo.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .toList();
    }
}
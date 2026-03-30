package com.capgemini.training.service;

import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
public class GroomingServiceService {

    @Autowired
    private GroomingServiceRepository repo;

    @Autowired
    private PetRepository petRepo;

   
    private GroomingServiceDTO convertToDTO(GroomingService s) {
        GroomingServiceDTO dto = new GroomingServiceDTO();

        dto.setServiceId(s.getServiceId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setPrice(s.getPrice() != null ? s.getPrice().doubleValue() : 0);
        dto.setAvailable(s.isAvailable());

        return dto;
    }

  
    private GroomingService convertToEntity(GroomingServiceDTO dto) {
        GroomingService s = new GroomingService();

        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setPrice(BigDecimal.valueOf(dto.getPrice()));
        s.setAvailable(dto.isAvailable());

        return s;
    }

    public List<GroomingServiceDTO> getAllFiltered(BigDecimal minPrice,
                                                   BigDecimal maxPrice) {
        return repo.findAll().stream()
                .filter(s -> minPrice == null || s.getPrice() == null
                        || s.getPrice().compareTo(minPrice) >= 0)
                .filter(s -> maxPrice == null || s.getPrice() == null
                        || s.getPrice().compareTo(maxPrice) <= 0)
                .map(this::convertToDTO)
                .toList();
    }

    public GroomingServiceDTO getById(int id) {
        return convertToDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found")));
    }

    public List<GroomingServiceDTO> getAvailable() {
        return repo.findByAvailable(true).stream()
                .map(this::convertToDTO).toList();
    }

    public List<GroomingServiceDTO> getUnavailable() {
        return repo.findByAvailable(false).stream()
                .map(this::convertToDTO).toList();
    }

    public GroomingServiceDTO add(GroomingServiceDTO dto) {
        return convertToDTO(repo.save(convertToEntity(dto)));
    }

    public GroomingServiceDTO update(int id, GroomingServiceDTO dto) {

        GroomingService s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setPrice(BigDecimal.valueOf(dto.getPrice()));
        s.setAvailable(dto.isAvailable());

        return convertToDTO(repo.save(s));
    }

    public void assignService(int petId, int serviceId) {

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        GroomingService s = repo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        boolean alreadyAssigned = pet.getGroomingServices().stream()
                .anyMatch(existing -> existing.getServiceId() == serviceId);
        if (!alreadyAssigned) {
            pet.getGroomingServices().add(s);
        }
        petRepo.save(pet);
    }

    public List<GroomingServiceDTO> getServicesByPet(int petId) {

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        return pet.getGroomingServices().stream()
                .map(this::convertToDTO)
                .toList();
    }
}
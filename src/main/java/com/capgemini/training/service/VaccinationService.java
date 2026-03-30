package com.capgemini.training.service;

import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.entity.Vaccination;
import com.capgemini.training.repository.PetRepository;
import com.capgemini.training.repository.VaccinationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VaccinationService {

    @Autowired
    private VaccinationRepository repo;

    @Autowired
    private PetRepository petRepo;

    private VaccinationDTO convertToDTO(Vaccination v) {
        VaccinationDTO dto = new VaccinationDTO();

        dto.setVaccinationId(v.getVaccinationId());
        dto.setName(v.getName());
        dto.setDescription(v.getDescription());
        dto.setPrice(v.getPrice() != null ? v.getPrice().doubleValue() : 0);
        dto.setAvailable(v.isAvailable());

        return dto;
    }

    private Vaccination convertToEntity(VaccinationDTO dto) {
        Vaccination v = new Vaccination();

        v.setName(dto.getName());
        v.setDescription(dto.getDescription());
        v.setPrice(BigDecimal.valueOf(dto.getPrice()));
        v.setAvailable(dto.isAvailable());

        return v;
    }

    public List<VaccinationDTO> getAllFiltered(BigDecimal minPrice, BigDecimal maxPrice) {
        return repo.findAll().stream()
                .filter(v -> minPrice == null || v.getPrice() == null
                        || v.getPrice().compareTo(minPrice) >= 0)
                .filter(v -> maxPrice == null || v.getPrice() == null
                        || v.getPrice().compareTo(maxPrice) <= 0)
                .map(this::convertToDTO)
                .toList();
    }

    public VaccinationDTO getById(int id) {
        return convertToDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination not found")));
    }

    public List<VaccinationDTO> getAvailable() {
        return repo.findByAvailable(true)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<VaccinationDTO> getUnavailable() {
        return repo.findByAvailable(false)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public VaccinationDTO add(VaccinationDTO dto) {
        return convertToDTO(repo.save(convertToEntity(dto)));
    }

    public VaccinationDTO update(int id, VaccinationDTO dto) {

        Vaccination v = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccination not found"));

        v.setName(dto.getName());
        v.setDescription(dto.getDescription());
        v.setPrice(BigDecimal.valueOf(dto.getPrice()));
        v.setAvailable(dto.isAvailable());

        return convertToDTO(repo.save(v));
    }

    public void assignToPet(int petId, int vaccinationId) {

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Vaccination v = repo.findById(vaccinationId)
                .orElseThrow(() -> new RuntimeException("Vaccination not found"));

        boolean alreadyAssigned = pet.getVaccinations().stream()
                .anyMatch(existing -> existing.getVaccinationId() == vaccinationId);

        if (!alreadyAssigned) {
            pet.getVaccinations().add(v);
        }

        petRepo.save(pet);
    }

    public List<VaccinationDTO> getVaccinationsByPet(int petId) {

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        return pet.getVaccinations().stream()
                .map(this::convertToDTO)
                .toList();
    }
}
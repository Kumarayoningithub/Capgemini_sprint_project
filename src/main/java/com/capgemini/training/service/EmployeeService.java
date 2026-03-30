
package com.capgemini.training.service;

import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repo;

    @Autowired
    private PetRepository petRepo;

    
    private EmployeeDTO convertToDTO(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(e.getEmployeeId());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setPosition(e.getPosition());
        dto.setHireDate(e.getHireDate());
        dto.setPhoneNumber(e.getPhoneNumber());
        dto.setEmail(e.getEmail());
        return dto;
    }

  
    private Employee convertToEntity(EmployeeDTO dto) {
        Employee e = new Employee();
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setPosition(dto.getPosition());
        e.setHireDate(dto.getHireDate());
        e.setPhoneNumber(dto.getPhoneNumber());
        e.setEmail(dto.getEmail());
        return e;
    }

    public List<EmployeeDTO> getAllFiltered(LocalDate minHireDate, LocalDate maxHireDate) {
        return repo.findAll().stream()
                .filter(e -> minHireDate == null || e.getHireDate() == null
                        || !e.getHireDate().isBefore(minHireDate))
                .filter(e -> maxHireDate == null || e.getHireDate() == null
                        || !e.getHireDate().isAfter(maxHireDate))
                .map(this::convertToDTO)
                .toList();
    }

    public EmployeeDTO getById(int id) {
        return convertToDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found")));
    }

    public List<EmployeeDTO> getByName(String name) {
        return repo.findByFirstNameContainingIgnoreCase(name)
                .stream().map(this::convertToDTO).toList();
    }

    public List<EmployeeDTO> getByPosition(String position) {
        return repo.findByPositionIgnoreCase(position)
                .stream().map(this::convertToDTO).toList();
    }

    public EmployeeDTO add(EmployeeDTO dto) {
        return convertToDTO(repo.save(convertToEntity(dto)));
    }

    public EmployeeDTO update(int id, EmployeeDTO dto) {
        Employee e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setPosition(dto.getPosition());
        e.setHireDate(dto.getHireDate());
        e.setPhoneNumber(dto.getPhoneNumber());
        e.setEmail(dto.getEmail());

        return convertToDTO(repo.save(e));
    }

    public void assignEmployeeToPet(int petId, int employeeId) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Employee emp = repo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        boolean alreadyAssigned = pet.getEmployees().stream()
                .anyMatch(existing -> existing.getEmployeeId() == employeeId);
        if (!alreadyAssigned) {
            pet.getEmployees().add(emp);
        }
        petRepo.save(pet);
    }

    public List<EmployeeDTO> getEmployeesByPet(int petId) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        return pet.getEmployees().stream()
                .map(this::convertToDTO)
                .toList();
    }
}
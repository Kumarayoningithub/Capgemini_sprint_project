package com.capgemini.training.controller;

import com.capgemini.training.dto.*;
import com.capgemini.training.service.VaccinationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vaccinations")
public class VaccinationController {

    @Autowired
    private VaccinationService service;

  
    @GetMapping
    public List<VaccinationDTO> getAll() {
        return service.getAll();
    }

  
    @GetMapping("/{id}")
    public VaccinationDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

 
    @GetMapping("/available")
    public List<VaccinationDTO> getAvailable() {
        return service.getAvailable();
    }

  
    @GetMapping("/unavailable")
    public List<VaccinationDTO> getUnavailable() {
        return service.getUnavailable();
    }


    @PostMapping("/add")
    public VaccinationDTO add(@RequestBody VaccinationDTO dto) {
        return service.add(dto);
    }

  
    @PutMapping("/update/{id}")
    public VaccinationDTO update(@PathVariable int id,
                                 @RequestBody VaccinationDTO dto) {
        return service.update(id, dto);
    }

    @PostMapping("/pets/{petId}/vaccinations/{vaccinationId}")
    public String assignToPet(@PathVariable int petId,
                             @PathVariable int vaccinationId) {
        service.assignToPet(petId, vaccinationId);
        return "Vaccination assigned to pet";
    }

    @GetMapping("/pets/{petId}/vaccinations")
    public List<VaccinationDTO> getByPet(@PathVariable int petId) {
        return service.getVaccinationsByPet(petId);
    }

 
    @PostMapping("/{vaccinationId}/employees/{employeeId}")
    public String assignEmployee(@PathVariable int vaccinationId,
                                 @PathVariable int employeeId) {
        service.assignEmployee(vaccinationId, employeeId);
        return "Employee assigned to vaccination";
    }

    @GetMapping("/{vaccinationId}/employees")
    public List<EmployeeDTO> getEmployees(@PathVariable int vaccinationId) {
        return service.getEmployeesByVaccination(vaccinationId);
    }
}

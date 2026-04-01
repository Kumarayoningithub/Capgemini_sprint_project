package com.capgemini.training.controller;

import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.service.EmployeeService;
import com.capgemini.training.service.GroomingServiceService;
import com.capgemini.training.service.PetService;
import com.capgemini.training.service.VaccinationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetRelationController {

    @Autowired
    private PetService petService;

    @Autowired
    private GroomingServiceService groomingService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VaccinationService vaccinationService;

    @PostMapping("/{petId}/foods/{foodId}")
    public PetDTO assignFood(@PathVariable int petId, @PathVariable int foodId) {
        return petService.assignFood(petId, foodId);
    }

    @PostMapping("/{petId}/suppliers/{supplierId}")
    public PetDTO assignSupplier(@PathVariable int petId, @PathVariable int supplierId) {
        return petService.assignSupplier(petId, supplierId);
    }

    @PostMapping("/{petId}/services/{serviceId}")
    public String assignService(@PathVariable int petId, @PathVariable int serviceId) {
        groomingService.assignService(petId, serviceId);
        return "Service assigned";
    }

    @GetMapping("/{petId}/services")
    public List<GroomingServiceDTO> getServicesByPet(@PathVariable int petId) {
        return groomingService.getServicesByPet(petId);
    }

    @PostMapping("/{petId}/vaccinations/{vaccinationId}")
    public String assignVaccination(@PathVariable int petId, @PathVariable int vaccinationId) {
        vaccinationService.assignToPet(petId, vaccinationId);
        return "Vaccination assigned to pet";
    }

    @GetMapping("/{petId}/vaccinations")
    public List<VaccinationDTO> getVaccinationsByPet(@PathVariable int petId) {
        return vaccinationService.getVaccinationsByPet(petId);
    }

    @PostMapping("/{petId}/employees/{employeeId}")
    public String assignEmployeeToPet(@PathVariable int petId, @PathVariable int employeeId) {
        employeeService.assignEmployeeToPet(petId, employeeId);
        return "Employee assigned";
    }

    @GetMapping("/{petId}/employees")
    public List<EmployeeDTO> getEmployeesByPet(@PathVariable int petId) {
        return employeeService.getEmployeesByPet(petId);
    }
}
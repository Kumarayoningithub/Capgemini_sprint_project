package com.capgemini.training.controller;

import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public List<EmployeeDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/name/{name}")
    public List<EmployeeDTO> getByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @GetMapping("/position/{position}")
    public List<EmployeeDTO> getByPosition(@PathVariable String position) {
        return service.getByPosition(position);
    }

    @PostMapping("/add")
    public EmployeeDTO add(@RequestBody EmployeeDTO dto) {
        return service.add(dto);
    }

    @PutMapping("/update/{id}")
    public EmployeeDTO update(@PathVariable int id,
                              @RequestBody EmployeeDTO dto) {
        return service.update(id, dto);
    }

    @PostMapping("/pets/{petId}/employees/{employeeId}")
    public String assignEmployeeToPet(@PathVariable int petId,
                                     @PathVariable int employeeId) {
        service.assignEmployeeToPet(petId, employeeId);
        return "Employee assigned";
    }

    @GetMapping("/pets/{petId}/employees")
    public List<EmployeeDTO> getEmployeesByPet(@PathVariable int petId) {
        return service.getEmployeesByPet(petId);
    }
}
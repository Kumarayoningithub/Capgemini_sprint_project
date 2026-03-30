package com.capgemini.training.controller;

import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public List<EmployeeDTO> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minHireDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maxHireDate) {
        return service.getAllFiltered(minHireDate, maxHireDate);
    }

    @GetMapping("/name/{name}")
    public List<EmployeeDTO> getByName(@PathVariable String name) {
        return service.getByName(name);
    }

    @GetMapping("/position/{position}")
    public List<EmployeeDTO> getByPosition(@PathVariable String position) {
        return service.getByPosition(position);
    }

    @GetMapping("/{id}")
    public EmployeeDTO getById(@PathVariable int id) {
        return service.getById(id);
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
}
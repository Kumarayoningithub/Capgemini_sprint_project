package com.capgemini.training.controller;

import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.service.VaccinationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vaccinations")
public class VaccinationController {

    @Autowired
    private VaccinationService service;

    @GetMapping
    public List<VaccinationDTO> getAll(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return service.getAllFiltered(minPrice, maxPrice);
    }

    @GetMapping("/available")
    public List<VaccinationDTO> getAvailable() {
        return service.getAvailable();
    }

    @GetMapping("/unavailable")
    public List<VaccinationDTO> getUnavailable() {
        return service.getUnavailable();
    }

    @GetMapping("/{id}")
    public VaccinationDTO getById(@PathVariable int id) {
        return service.getById(id);
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

 }
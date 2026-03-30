package com.capgemini.training.controller;

import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.service.GroomingServiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/services")
public class GroomingController {

    @Autowired
    private GroomingServiceService service;

    @GetMapping
    public List<GroomingServiceDTO> getAll(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return service.getAllFiltered(minPrice, maxPrice);
    }

    @GetMapping("/available")
    public List<GroomingServiceDTO> getAvailable() {
        return service.getAvailable();
    }

    @GetMapping("/unavailable")
    public List<GroomingServiceDTO> getUnavailable() {
        return service.getUnavailable();
    }

    @GetMapping("/{id}")
    public GroomingServiceDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/add")
    public GroomingServiceDTO add(@RequestBody GroomingServiceDTO dto) {
        return service.add(dto);
    }

    @PutMapping("/update/{id}")
    public GroomingServiceDTO update(@PathVariable int id,
                                    @RequestBody GroomingServiceDTO dto) {
        return service.update(id, dto);
    }
}
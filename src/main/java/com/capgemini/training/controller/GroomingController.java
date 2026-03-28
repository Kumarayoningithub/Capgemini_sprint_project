package com.capgemini.training.controller;

import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.service.GroomingServiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class GroomingController {

    @Autowired
    private GroomingServiceService service;

    @GetMapping
    public List<GroomingServiceDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public GroomingServiceDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @GetMapping("/available")
    public List<GroomingServiceDTO> getAvailable() {
        return service.getAvailable();
    }

    @GetMapping("/unavailable")
    public List<GroomingServiceDTO> getUnavailable() {
        return service.getUnavailable();
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

    @PostMapping("/pets/{petId}/services/{serviceId}")
    public String assign(@PathVariable int petId, @PathVariable int serviceId) {
        service.assignService(petId, serviceId);
        return "Service assigned";
    }

    @GetMapping("/pets/{petId}/services")
    public List<GroomingServiceDTO> getServicesByPet(@PathVariable int petId) {
        return service.getServicesByPet(petId);
    }
}

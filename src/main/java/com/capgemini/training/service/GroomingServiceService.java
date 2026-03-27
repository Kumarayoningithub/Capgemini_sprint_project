package com.capgemini.training.service;

import com.capgemini.training.entity.GroomingService;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.repository.GroomingServiceRepository;
import com.capgemini.training.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroomingServiceService {

    @Autowired
    private GroomingServiceRepository repo;

    @Autowired
    private PetRepository petRepo;

    public List<GroomingService> getAll() {
        return repo.findAll();
    }

    public GroomingService getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<GroomingService> getAvailable() {
        return repo.findByAvailable(true);
    }

    public List<GroomingService> getUnavailable() {
        return repo.findByAvailable(false);
    }

    public GroomingService add(GroomingService s) {
        return repo.save(s);
    }

    public GroomingService update(int id, GroomingService updated) {
        GroomingService s = repo.findById(id).orElse(null);
        if (s != null) {
            s.setName(updated.getName());
            s.setPrice(updated.getPrice());
            s.setAvailable(updated.isAvailable());
            return repo.save(s);
        }
        return null;
    }

    public Pet assignService(int petId, int serviceId) {
        Pet pet = petRepo.findById(petId).orElse(null);
        GroomingService s = repo.findById(serviceId).orElse(null);

        if (pet != null && s != null) {
            pet.getGroomingServices().add(s);
            return petRepo.save(pet);
        }
        return null;
    }

    public List<GroomingService> getServicesByPet(int petId) {
        Pet pet = petRepo.findById(petId).orElse(null);
        if (pet != null) {
            return pet.getGroomingServices();
        }
        return null;
    }
}
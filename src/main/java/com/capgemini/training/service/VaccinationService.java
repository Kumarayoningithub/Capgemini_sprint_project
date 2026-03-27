package com.capgemini.training.service;



import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccinationService {

    @Autowired
    private VaccinationRepository repo;

    @Autowired
    private PetRepository petRepo;

    public List<Vaccination> getAll() {
        return repo.findAll();
    }

    public Vaccination add(Vaccination v) {
        return repo.save(v);
    }

    public Pet assignVaccination(int petId, int vaccinationId) {
        Pet pet = petRepo.findById(petId).orElse(null);
        Vaccination v = repo.findById(vaccinationId).orElse(null);

        if (pet != null && v != null) {
            pet.getVaccinations().add(v);
            return petRepo.save(pet);
        }
        return null;
    }
}
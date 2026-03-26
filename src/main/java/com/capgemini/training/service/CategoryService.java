package com.capgemini.training.service;



import com.capgemini.training.entity.PetCategory;

import com.capgemini.training.repository.PetCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private PetCategoryRepository repository;

    public List<PetCategory> getAllCategories() {
        return repository.findAll();
    }

    public PetCategory getById(int id) {
        return repository.findById(id).orElse(null);
    }

    public PetCategory add(PetCategory category) {
        return repository.save(category);
    }

    public PetCategory update(int id, PetCategory updated) {
        PetCategory c = getById(id);
        if (c != null) {
            c.setName(updated.getName());
            return repository.save(c);
        }
        return null;
    }
}
package com.capgemini.training.service;

import com.capgemini.training.entity.PetFood;
import com.capgemini.training.repository.PetFoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetFoodService {

    @Autowired
    private PetFoodRepository repo;

   
    public List<PetFood> getAll() {
        return repo.findAll();
    }

   
    public PetFood getById(int id) {
        return repo.findById(id).orElse(null);
    }

   
    public PetFood add(PetFood food) {
        return repo.save(food);
    }

  
    public PetFood update(int id, PetFood updated) {
        PetFood f = getById(id);
        if (f != null) {
            f.setName(updated.getName());
            f.setBrand(updated.getBrand());
            f.setType(updated.getType());
            f.setQuantity(updated.getQuantity());
            f.setPrice(updated.getPrice());
            return repo.save(f);
        }
        return null;
    }

   
    public PetFood updateQuantity(int id, int quantity) {
        PetFood f = getById(id);
        if (f != null) {
            f.setQuantity(quantity);
            return repo.save(f);
        }
        return null;
    }

   
    public List<PetFood> getByType(String type) {
        return repo.findByType(type);
    }

    
    public List<PetFood> getByBrand(String brand) {
        return repo.findByBrand(brand);
    }

    
    public List<PetFood> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

  
    public List<PetFood> getLowStock(int quantity) {
        return repo.findByQuantityLessThanEqual(quantity);
    }

   
    public List<PetFood> getByPriceRange(double min, double max) {
        return repo.findByPriceBetween(min, max);
    }
}
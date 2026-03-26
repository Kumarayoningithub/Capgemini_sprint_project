package com.capgemini.training.repository;

import java.util.List;
import com.capgemini.training.entity.PetFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetFoodRepository extends JpaRepository<PetFood, Integer> {

   
    List<PetFood> findByType(String type);

    
    List<PetFood> findByBrand(String brand);

   
    List<PetFood> findByNameContainingIgnoreCase(String name);

  
    List<PetFood> findByQuantityLessThanEqual(int quantity);

  
    List<PetFood> findByPriceBetween(double min, double max);
}
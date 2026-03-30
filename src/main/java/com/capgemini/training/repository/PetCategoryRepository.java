package com.capgemini.training.repository;

import com.capgemini.training.entity.PetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetCategoryRepository extends JpaRepository<PetCategory, Integer> {

  
    PetCategory findByName(String name);
}
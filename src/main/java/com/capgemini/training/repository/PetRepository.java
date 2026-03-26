package com.capgemini.training.repository;

import com.capgemini.training.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {

    List<Pet> findByCategoryCategoryId(int categoryId);
}
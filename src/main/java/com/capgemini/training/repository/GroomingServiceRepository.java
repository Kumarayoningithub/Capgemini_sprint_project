package com.capgemini.training.repository;

import com.capgemini.training.entity.GroomingService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroomingServiceRepository extends JpaRepository<GroomingService, Integer> {

  
    List<GroomingService> findByAvailable(boolean available);
}
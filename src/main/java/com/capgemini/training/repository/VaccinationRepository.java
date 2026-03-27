package com.capgemini.training.repository;



import com.capgemini.training.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {
}
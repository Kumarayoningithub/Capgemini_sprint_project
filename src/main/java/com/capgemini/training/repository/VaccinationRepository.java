package com.capgemini.training.repository;
import com.capgemini.training.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {

    List<Vaccination> findByAvailable(boolean available);
}
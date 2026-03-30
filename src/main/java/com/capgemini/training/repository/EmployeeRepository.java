package com.capgemini.training.repository;

import com.capgemini.training.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  

    List<Employee> findByFirstNameContainingIgnoreCase(String firstName);

    List<Employee> findByPositionIgnoreCase(String position);
}
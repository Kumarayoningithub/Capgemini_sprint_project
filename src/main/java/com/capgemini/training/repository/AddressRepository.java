package com.capgemini.training.repository;


import com.capgemini.training.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}

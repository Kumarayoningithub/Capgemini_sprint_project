package com.capgemini.training.repository;


import com.capgemini.training.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByCustomerCustomerId(int customerId);
    List<Transaction> findByPetPetId(int petId);
}

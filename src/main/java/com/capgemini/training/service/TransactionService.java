package com.capgemini.training.service;



import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PetRepository petRepo;

    public List<Transaction> getAll() {
        return repo.findAll();
    }

    public Transaction purchase(int customerId, int petId) {

        Customer customer = customerRepo.findById(customerId).orElse(null);
        Pet pet = petRepo.findById(petId).orElse(null);

        if (customer != null && pet != null) {

            Transaction t = new Transaction();
            t.setCustomer(customer);
            t.setPet(pet);
            t.setTransactionDate(LocalDate.now());
            t.setAmount(pet.getPrice());
            t.setTransactionStatus(TransactionStatus.Success);

            return repo.save(t);
        }
        return null;
    }
}
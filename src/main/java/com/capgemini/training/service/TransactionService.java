package com.capgemini.training.service;

import com.capgemini.training.dto.TransactionDTO;
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

    private TransactionDTO convertToDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();

        dto.setTransactionId(t.getTransactionId());
        dto.setAmount(t.getAmount());

       
        dto.setStatus(t.getTransactionStatus().name());

        dto.setTransactionDate(t.getTransactionDate().toString());

        dto.setCustomerId(t.getCustomer().getCustomerId());
        dto.setCustomerName(
                t.getCustomer().getFirstName() + " " + t.getCustomer().getLastName()
        );

        dto.setPetId(t.getPet().getPetId());
        dto.setPetName(t.getPet().getName());

        return dto;
    }


    public List<TransactionDTO> getAll(LocalDate minDate, LocalDate maxDate) {
        return repo.findAll().stream()
                .filter(t -> minDate == null || !t.getTransactionDate().isBefore(minDate))
                .filter(t -> maxDate == null || !t.getTransactionDate().isAfter(maxDate))
                .map(this::convertToDTO)
                .toList();
    }


    public TransactionDTO getById(int id) {
        Transaction t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return convertToDTO(t);
    }

 
    public List<TransactionDTO> getByCustomer(int customerId) {
        return repo.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<TransactionDTO> getSuccessTransactions() {
        return repo.findAll().stream()
                .filter(t -> t.getTransactionStatus() == TransactionStatus.Success)
                .map(this::convertToDTO)
                .toList();
    }


    public List<TransactionDTO> getFailedTransactions() {
        return repo.findAll().stream()
                .filter(t -> t.getTransactionStatus() == TransactionStatus.Failed)
                .map(this::convertToDTO)
                .toList();
    }

    public TransactionDTO add(TransactionDTO dto) {

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Pet pet = petRepo.findById(dto.getPetId())
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        Transaction t = new Transaction();
        t.setCustomer(customer);
        t.setPet(pet);
        t.setAmount(dto.getAmount());
        t.setTransactionDate(LocalDate.now());

  
        t.setTransactionStatus(TransactionStatus.valueOf(dto.getStatus()));

        return convertToDTO(repo.save(t));
    }
}
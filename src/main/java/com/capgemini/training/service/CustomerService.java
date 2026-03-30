package com.capgemini.training.service;


import com.capgemini.training.dto.CustomerDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.CustomerRepository;
import com.capgemini.training.repository.AddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private AddressRepository addressRepository;

  
    private CustomerDTO convertToDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();

        dto.setCustomerId(c.getCustomerId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setPhoneNumber(c.getPhoneNumber());

        if (c.getAddress() != null) {
            dto.setAddressId(c.getAddress().getAddressId());
            dto.setCity(c.getAddress().getCity());
            dto.setState(c.getAddress().getState());
        }

        return dto;
    }


    private Customer convertToEntity(CustomerDTO dto) {
        Customer c = new Customer();

        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());
        c.setAddress(resolveAddress(dto));

        return c;
    }

  
    public List<CustomerDTO> getAllCustomers() {
        return repo.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

 
    public CustomerDTO getById(int id) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return convertToDTO(c);
    }

 
    public List<CustomerDTO> getByName(String first, String last) {
        return repo.findByFirstNameAndLastName(first, last)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

   
    public List<CustomerDTO> getByCity(String city) {
        return repo.findByAddress_CityIgnoreCase(city)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    public List<CustomerDTO> getByState(String state) {
        return repo.findByAddress_StateIgnoreCase(state)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public CustomerDTO add(CustomerDTO dto) {
        Customer saved = repo.save(convertToEntity(dto));
        return convertToDTO(saved);
    }


    public CustomerDTO update(int id, CustomerDTO dto) {

        Customer c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());
        c.setAddress(resolveAddress(dto));

        return convertToDTO(repo.save(c));
    }

 
    public List<CustomerDTO> getCustomersWithoutTransactions() {
        return repo.findAll().stream()
                .filter(c -> c.getTransactions() == null || c.getTransactions().isEmpty())
                .map(this::convertToDTO)
                .toList();
    }


    public List<TransactionDTO> getTransactions(int customerId) {
        Customer c = repo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return c.getTransactions().stream().map(t -> {
            TransactionDTO dto = new TransactionDTO();

            dto.setTransactionId(t.getTransactionId());
            dto.setAmount(t.getAmount());


            dto.setStatus(t.getTransactionStatus().name());

            dto.setTransactionDate(t.getTransactionDate().toString());

            dto.setCustomerId(c.getCustomerId());
            dto.setCustomerName(c.getFirstName() + " " + c.getLastName());

            dto.setPetId(t.getPet().getPetId());
            dto.setPetName(t.getPet().getName());

            return dto;
        }).toList();
    }

    public List<PetDTO> getCustomerPets(int customerId) {
        Customer c = repo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return c.getTransactions().stream().map(t -> {
            Pet p = t.getPet();

            PetDTO dto = new PetDTO();
            dto.setPetId(p.getPetId());
            dto.setName(p.getName());
            dto.setBreed(p.getBreed());

            return dto;
        }).toList();
    }

    public List<TransactionDTO> getTransactionsByStatus(String status) {
        return getAllCustomers().stream()
                .flatMap(customer -> getTransactions(customer.getCustomerId()).stream())
                .filter(transaction -> transaction.getStatus() != null
                        && transaction.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    private Address resolveAddress(CustomerDTO dto) {
        Integer addressId = dto.getAddressId();
        if (addressId == null && dto.getAddress() != null) {
            addressId = dto.getAddress().getAddressId();
        }
        if (addressId == null) {
            return null;
        }
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }
}
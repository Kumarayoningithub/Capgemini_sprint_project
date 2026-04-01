package com.capgemini.training.servicetest;

import com.capgemini.training.dto.CustomerDTO;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.AddressRepository;
import com.capgemini.training.repository.CustomerRepository;
import com.capgemini.training.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repo;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerService service;

    private Address address;
    private Customer customer1;
    private Customer customer2;
    private Pet pet;
    private Transaction txSuccess;
    private Transaction txFailed;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setAddressId(1);
        address.setCity("Kolkata");
        address.setState("West Bengal");

        customer1 = new Customer();
        customer1.setCustomerId(1);
        customer1.setFirstName("Alice");
        customer1.setLastName("Johnson");
        customer1.setEmail("alice@example.com");
        customer1.setPhoneNumber("5551234000");
        customer1.setAddress(address);
        customer1.setTransactions(new ArrayList<>());

        customer2 = new Customer();
        customer2.setCustomerId(2);
        customer2.setFirstName("Bob");
        customer2.setLastName("Smith");
        customer2.setEmail("bob@example.com");
        customer2.setPhoneNumber("5555678001");
        customer2.setAddress(null);
        customer2.setTransactions(new ArrayList<>());

        pet = new Pet();
        pet.setPetId(1);
        pet.setName("Bella");
        pet.setBreed("Labrador");

        txSuccess = new Transaction();
        txSuccess.setTransactionId(1);
        txSuccess.setAmount(new BigDecimal("25000.00"));
        txSuccess.setTransactionDate(LocalDate.of(2024, 1, 15));
        txSuccess.setTransactionStatus(TransactionStatus.Success);
        txSuccess.setCustomer(customer1);
        txSuccess.setPet(pet);

        txFailed = new Transaction();
        txFailed.setTransactionId(2);
        txFailed.setAmount(new BigDecimal("15000.00"));
        txFailed.setTransactionDate(LocalDate.of(2024, 3, 20));
        txFailed.setTransactionStatus(TransactionStatus.Failed);
        txFailed.setCustomer(customer1);
        txFailed.setPet(pet);
    }

    

    @Test
    @DisplayName("getAllCustomers - returns all customers as DTOs")
    void getAllCustomers_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = service.getAllCustomers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.get(1).getFirstName()).isEqualTo("Bob");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCustomers - empty repo - returns empty list")
    void getAllCustomers_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDTO> result = service.getAllCustomers();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllCustomers - customer with address - city and state mapped")
    void getAllCustomers_withAddress_cityAndStateMapped() {
        when(repo.findAll()).thenReturn(List.of(customer1));

        List<CustomerDTO> result = service.getAllCustomers();

        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
        assertThat(result.get(0).getState()).isEqualTo("West Bengal");
        assertThat(result.get(0).getAddressId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAllCustomers - customer without address - city and state are null")
    void getAllCustomers_withoutAddress_cityAndStateNull() {
        when(repo.findAll()).thenReturn(List.of(customer2));

        List<CustomerDTO> result = service.getAllCustomers();

        assertThat(result.get(0).getCity()).isNull();
        assertThat(result.get(0).getState()).isNull();
    }

   

    @Test
    @DisplayName("getById - found - returns correct DTO")
    void getById_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(customer1));

        CustomerDTO result = service.getById(1);

        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("5551234000");
        assertThat(result.getCity()).isEqualTo("Kolkata");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }

  

    @Test
    @DisplayName("getByName - match found - returns matching customers")
    void getByName_matchFound_returnsMatching() {
        when(repo.findByFirstNameAndLastName("Alice", "Johnson")).thenReturn(List.of(customer1));

        List<CustomerDTO> result = service.getByName("Alice", "Johnson");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("getByName - no match - returns empty list")
    void getByName_noMatch_returnsEmpty() {
        when(repo.findByFirstNameAndLastName("Unknown", "Person")).thenReturn(Collections.emptyList());

        List<CustomerDTO> result = service.getByName("Unknown", "Person");

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getByCity - city exists - returns customers in city")
    void getByCity_exists_returnsCustomers() {
        when(repo.findByAddress_CityIgnoreCase("Kolkata")).thenReturn(List.of(customer1));

        List<CustomerDTO> result = service.getByCity("Kolkata");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
    }

  

    @Test
    @DisplayName("getByState - state exists - returns customers in state")
    void getByState_exists_returnsCustomers() {
        when(repo.findByAddress_StateIgnoreCase("West Bengal")).thenReturn(List.of(customer1));

        List<CustomerDTO> result = service.getByState("West Bengal");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo("West Bengal");
    }

    

    @Test
    @DisplayName("add - valid DTO with address - saves and returns DTO")
    void add_validDTOWithAddress_savesAndReturns() {
        CustomerDTO inputDTO = new CustomerDTO();
        inputDTO.setFirstName("Charlie");
        inputDTO.setLastName("Williams");
        inputDTO.setEmail("charlie@example.com");
        inputDTO.setPhoneNumber("5559012002");
        inputDTO.setAddressId(1);

        Customer savedEntity = new Customer();
        savedEntity.setCustomerId(3);
        savedEntity.setFirstName("Charlie");
        savedEntity.setLastName("Williams");
        savedEntity.setEmail("charlie@example.com");
        savedEntity.setPhoneNumber("5559012002");
        savedEntity.setAddress(address);

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(repo.save(any(Customer.class))).thenReturn(savedEntity);

        CustomerDTO result = service.add(inputDTO);

        assertThat(result.getCustomerId()).isEqualTo(3);
        assertThat(result.getFirstName()).isEqualTo("Charlie");
        assertThat(result.getCity()).isEqualTo("Kolkata");
        verify(repo, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("add - address not found - throws RuntimeException")
    void add_addressNotFound_throwsException() {
        CustomerDTO inputDTO = new CustomerDTO();
        inputDTO.setFirstName("Test");
        inputDTO.setAddressId(999);

        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Address not found");
    }

  

    @Test
    @DisplayName("update - found - updates all fields and returns updated DTO")
    void update_found_updatesAndReturns() {
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setFirstName("Alice");
        updateDTO.setLastName("Johnson");
        updateDTO.setEmail("alice.updated@example.com");
        updateDTO.setPhoneNumber("9999999999");
        updateDTO.setAddressId(1);

        Customer updatedEntity = new Customer();
        updatedEntity.setCustomerId(1);
        updatedEntity.setFirstName("Alice");
        updatedEntity.setLastName("Johnson");
        updatedEntity.setEmail("alice.updated@example.com");
        updatedEntity.setPhoneNumber("9999999999");
        updatedEntity.setAddress(address);

        when(repo.findById(1)).thenReturn(Optional.of(customer1));
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(repo.save(any(Customer.class))).thenReturn(updatedEntity);

        CustomerDTO result = service.update(1, updateDTO);

        assertThat(result.getEmail()).isEqualTo("alice.updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("9999999999");
        verify(repo).findById(1);
        verify(repo).save(any(Customer.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, new CustomerDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }



    @Test
    @DisplayName("getCustomersWithoutTransactions - returns customers with empty transactions")
    void getCustomersWithoutTransactions_returnsCustomersWithEmptyTransactions() {
        customer1.setTransactions(new ArrayList<>());
        customer2.setTransactions(new ArrayList<>());
        when(repo.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = service.getCustomersWithoutTransactions();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getCustomersWithoutTransactions - customer with transactions - excluded")
    void getCustomersWithoutTransactions_customerWithTransactions_excluded() {
        customer1.setTransactions(List.of(txSuccess));
        customer2.setTransactions(new ArrayList<>());
        when(repo.findAll()).thenReturn(List.of(customer1, customer2));

        List<CustomerDTO> result = service.getCustomersWithoutTransactions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Bob");
    }



    @Test
    @DisplayName("getTransactions - customer found with transactions - returns transaction DTOs")
    void getTransactions_customerFound_returnsTransactionDTOs() {
        customer1.setTransactions(List.of(txSuccess, txFailed));
        when(repo.findById(1)).thenReturn(Optional.of(customer1));

        List<TransactionDTO> result = service.getTransactions(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
        assertThat(result.get(1).getStatus()).isEqualTo("Failed");
        assertThat(result.get(0).getCustomerName()).isEqualTo("Alice Johnson");
        assertThat(result.get(0).getPetName()).isEqualTo("Bella");
    }

    @Test
    @DisplayName("getTransactions - customer not found - throws RuntimeException")
    void getTransactions_customerNotFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTransactions(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }

 

    @Test
    @DisplayName("getCustomerPets - customer has transactions - returns pet DTOs")
    void getCustomerPets_customerHasTransactions_returnsPetDTOs() {
        customer1.setTransactions(List.of(txSuccess));
        when(repo.findById(1)).thenReturn(Optional.of(customer1));

        List<PetDTO> result = service.getCustomerPets(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bella");
        assertThat(result.get(0).getBreed()).isEqualTo("Labrador");
    }

   

    @Test
    @DisplayName("getTransactionsByStatus - Success - returns only successful transactions")
    void getTransactionsByStatus_success_returnsSuccessful() {
        customer1.setTransactions(List.of(txSuccess, txFailed));
        when(repo.findAll()).thenReturn(List.of(customer1));
        when(repo.findById(1)).thenReturn(Optional.of(customer1));

        List<TransactionDTO> result = service.getTransactionsByStatus("Success");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
    }
}

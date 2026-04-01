package com.capgemini.training.servicetest;

import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.CustomerRepository;
import com.capgemini.training.repository.PetRepository;
import com.capgemini.training.repository.TransactionRepository;
import com.capgemini.training.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private TransactionService service;

    private Customer customer;
    private Pet pet;
    private Transaction txSuccess;
    private Transaction txFailed;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setFirstName("Alice");
        customer.setLastName("Johnson");
        customer.setEmail("alice@example.com");
        customer.setPhoneNumber("5551234000");

        pet = new Pet();
        pet.setPetId(1);
        pet.setName("Bella");
        pet.setBreed("Labrador");

        txSuccess = new Transaction();
        txSuccess.setTransactionId(1);
        txSuccess.setAmount(new BigDecimal("25000.00"));
        txSuccess.setTransactionDate(LocalDate.of(2024, 1, 15));
        txSuccess.setTransactionStatus(TransactionStatus.Success);
        txSuccess.setCustomer(customer);
        txSuccess.setPet(pet);

        txFailed = new Transaction();
        txFailed.setTransactionId(2);
        txFailed.setAmount(new BigDecimal("15000.00"));
        txFailed.setTransactionDate(LocalDate.of(2024, 3, 20));
        txFailed.setTransactionStatus(TransactionStatus.Failed);
        txFailed.setCustomer(customer);
        txFailed.setPet(pet);
    }


    @Test
    @DisplayName("getAll - no date filters - returns all transactions")
    void getAll_noFilters_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getAll(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
        assertThat(result.get(1).getStatus()).isEqualTo("Failed");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - minDate filter - returns only transactions after date")
    void getAll_minDateFilter_returnsAfterDate() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getAll(LocalDate.of(2024, 2, 1), null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Failed");
    }

    @Test
    @DisplayName("getAll - maxDate filter - returns only transactions before date")
    void getAll_maxDateFilter_returnsBeforeDate() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getAll(null, LocalDate.of(2024, 2, 28));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
    }

    @Test
    @DisplayName("getAll - both date filters - returns transactions in range")
    void getAll_bothFilters_returnsInRange() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getAll(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 28));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAll - empty repo - returns empty list")
    void getAll_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = service.getAll(null, null);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getById - found - returns correct DTO with all fields")
    void getById_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(txSuccess));

        TransactionDTO result = service.getById(1);

        assertThat(result.getTransactionId()).isEqualTo(1);
        assertThat(result.getAmount()).isEqualByComparingTo("25000.00");
        assertThat(result.getStatus()).isEqualTo("Success");
        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getCustomerName()).isEqualTo("Alice Johnson");
        assertThat(result.getPetId()).isEqualTo(1);
        assertThat(result.getPetName()).isEqualTo("Bella");
        assertThat(result.getTransactionDate()).isEqualTo("2024-01-15");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Transaction not found");
    }


    @Test
    @DisplayName("getByCustomer - customer has transactions - returns list")
    void getByCustomer_hasTransactions_returnsList() {
        when(repo.findByCustomerCustomerId(1)).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getByCustomer(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1);
        assertThat(result.get(1).getCustomerId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getByCustomer - no transactions - returns empty list")
    void getByCustomer_noTransactions_returnsEmpty() {
        when(repo.findByCustomerCustomerId(99)).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = service.getByCustomer(99);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getSuccessTransactions - returns only successful transactions")
    void getSuccessTransactions_returnsOnlySuccessful() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getSuccessTransactions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
    }

    @Test
    @DisplayName("getSuccessTransactions - none successful - returns empty list")
    void getSuccessTransactions_noneSuccessful_returnsEmpty() {
        when(repo.findAll()).thenReturn(List.of(txFailed));

        List<TransactionDTO> result = service.getSuccessTransactions();

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getFailedTransactions - returns only failed transactions")
    void getFailedTransactions_returnsOnlyFailed() {
        when(repo.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getFailedTransactions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("Failed");
    }

    @Test
    @DisplayName("getFailedTransactions - none failed - returns empty list")
    void getFailedTransactions_noneFailed_returnsEmpty() {
        when(repo.findAll()).thenReturn(List.of(txSuccess));

        List<TransactionDTO> result = service.getFailedTransactions();

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("add - valid DTO - saves and returns DTO")
    void add_validDTO_savesAndReturnsDTO() {
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setCustomerId(1);
        inputDTO.setPetId(1);
        inputDTO.setAmount(new BigDecimal("30000.00"));
        inputDTO.setStatus("Success");

        Transaction savedEntity = new Transaction();
        savedEntity.setTransactionId(3);
        savedEntity.setCustomer(customer);
        savedEntity.setPet(pet);
        savedEntity.setAmount(new BigDecimal("30000.00"));
        savedEntity.setTransactionDate(LocalDate.now());
        savedEntity.setTransactionStatus(TransactionStatus.Success);

        when(customerRepo.findById(1)).thenReturn(Optional.of(customer));
        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.save(any(Transaction.class))).thenReturn(savedEntity);

        TransactionDTO result = service.add(inputDTO);

        assertThat(result.getTransactionId()).isEqualTo(3);
        assertThat(result.getStatus()).isEqualTo("Success");
        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getPetId()).isEqualTo(1);
        verify(repo, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("add - customer not found - throws RuntimeException")
    void add_customerNotFound_throwsException() {
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setCustomerId(99);
        inputDTO.setPetId(1);
        inputDTO.setStatus("Success");

        when(customerRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Customer not found");
    }

    @Test
    @DisplayName("add - pet not found - throws RuntimeException")
    void add_petNotFound_throwsException() {
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setCustomerId(1);
        inputDTO.setPetId(99);
        inputDTO.setStatus("Success");

        when(customerRepo.findById(1)).thenReturn(Optional.of(customer));
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }


    @Test
    @DisplayName("getById - DTO customer name is first + last name combined")
    void getById_customerNameCombined() {
        when(repo.findById(1)).thenReturn(Optional.of(txSuccess));

        TransactionDTO result = service.getById(1);

        assertThat(result.getCustomerName()).isEqualTo("Alice Johnson");
    }

    @Test
    @DisplayName("getById - transaction date is formatted as String")
    void getById_transactionDateFormattedAsString() {
        when(repo.findById(1)).thenReturn(Optional.of(txSuccess));

        TransactionDTO result = service.getById(1);

        assertThat(result.getTransactionDate()).isEqualTo("2024-01-15");
    }
}

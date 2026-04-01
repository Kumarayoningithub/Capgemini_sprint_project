package com.capgemini.training.servicetest;

import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.entity.Address;
import com.capgemini.training.entity.Supplier;
import com.capgemini.training.repository.AddressRepository;
import com.capgemini.training.repository.SupplierRepository;
import com.capgemini.training.service.SupplierService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository repo;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private SupplierService service;

    private Address address;
    private Supplier supplier1;
    private Supplier supplier2;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setAddressId(1);
        address.setStreet("123 Park Street");
        address.setCity("Kolkata");
        address.setState("West Bengal");
        address.setZipCode("700001");

        supplier1 = new Supplier();
        supplier1.setSupplierId(1);
        supplier1.setName("Pet World Supplier");
        supplier1.setContactPerson("Ramesh Gupta");
        supplier1.setPhoneNumber("9988776655");
        supplier1.setEmail("supplier@example.com");
        supplier1.setAddress(address);

        supplier2 = new Supplier();
        supplier2.setSupplierId(2);
        supplier2.setName("Delhi Pet Mart");
        supplier2.setContactPerson("Neha Verma");
        supplier2.setPhoneNumber("9123456789");
        supplier2.setEmail("delhi@example.com");
        supplier2.setAddress(null);
    }

  

    @Test
    @DisplayName("getAllDTO - returns all suppliers as DTOs")
    void getAllDTO_returnsAllSuppliers() {
        when(repo.findAll()).thenReturn(List.of(supplier1, supplier2));

        List<SupplierDTO> result = service.getAllDTO();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Pet World Supplier");
        assertThat(result.get(1).getName()).isEqualTo("Delhi Pet Mart");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllDTO - empty repo - returns empty list")
    void getAllDTO_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<SupplierDTO> result = service.getAllDTO();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllDTO - supplier with address - city and state are mapped")
    void getAllDTO_supplierWithAddress_cityAndStateMapped() {
        when(repo.findAll()).thenReturn(List.of(supplier1));

        List<SupplierDTO> result = service.getAllDTO();

        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
        assertThat(result.get(0).getState()).isEqualTo("West Bengal");
        assertThat(result.get(0).getAddressId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAllDTO - supplier without address - city and state are null")
    void getAllDTO_supplierWithoutAddress_cityAndStateNull() {
        when(repo.findAll()).thenReturn(List.of(supplier2));

        List<SupplierDTO> result = service.getAllDTO();

        assertThat(result.get(0).getCity()).isNull();
        assertThat(result.get(0).getState()).isNull();
    }



    @Test
    @DisplayName("getByIdDTO - found - returns correct DTO")
    void getByIdDTO_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(supplier1));

        SupplierDTO result = service.getByIdDTO(1);

        assertThat(result.getSupplierId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Pet World Supplier");
        assertThat(result.getContactPerson()).isEqualTo("Ramesh Gupta");
        assertThat(result.getPhoneNumber()).isEqualTo("9988776655");
        assertThat(result.getEmail()).isEqualTo("supplier@example.com");
        assertThat(result.getCity()).isEqualTo("Kolkata");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getByIdDTO - not found - throws RuntimeException")
    void getByIdDTO_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdDTO(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Supplier not found");
    }


    @Test
    @DisplayName("add - valid DTO with address - saves and returns DTO")
    void add_validDTOWithAddress_savesAndReturnsDTO() {
        SupplierDTO inputDTO = new SupplierDTO();
        inputDTO.setName("Mumbai Wholesale");
        inputDTO.setContactPerson("Priya Nair");
        inputDTO.setPhoneNumber("9000000000");
        inputDTO.setEmail("mumbai@example.com");
        inputDTO.setAddressId(1);

        Supplier savedEntity = new Supplier();
        savedEntity.setSupplierId(3);
        savedEntity.setName("Mumbai Wholesale");
        savedEntity.setContactPerson("Priya Nair");
        savedEntity.setPhoneNumber("9000000000");
        savedEntity.setEmail("mumbai@example.com");
        savedEntity.setAddress(address);

        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(repo.save(any(Supplier.class))).thenReturn(savedEntity);

        SupplierDTO result = service.add(inputDTO);

        assertThat(result.getSupplierId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Mumbai Wholesale");
        assertThat(result.getCity()).isEqualTo("Kolkata");
        verify(repo, times(1)).save(any(Supplier.class));
    }

    @Test
    @DisplayName("add - valid DTO without address - saves with null address")
    void add_validDTOWithoutAddress_savesWithNullAddress() {
        SupplierDTO inputDTO = new SupplierDTO();
        inputDTO.setName("Simple Supplier");
        inputDTO.setContactPerson("Test Person");
        inputDTO.setPhoneNumber("1234567890");
        inputDTO.setEmail("test@test.com");

        Supplier savedEntity = new Supplier();
        savedEntity.setSupplierId(4);
        savedEntity.setName("Simple Supplier");
        savedEntity.setAddress(null);

        when(repo.save(any(Supplier.class))).thenReturn(savedEntity);

        SupplierDTO result = service.add(inputDTO);

        assertThat(result.getName()).isEqualTo("Simple Supplier");
        assertThat(result.getCity()).isNull();
    }

    @Test
    @DisplayName("add - address id not found - throws RuntimeException")
    void add_addressNotFound_throwsException() {
        SupplierDTO inputDTO = new SupplierDTO();
        inputDTO.setName("Test");
        inputDTO.setAddressId(999);

        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Address not found");
    }

  

    @Test
    @DisplayName("update - found - updates all fields and returns updated DTO")
    void update_found_updatesAndReturnsDTO() {
        SupplierDTO updateDTO = new SupplierDTO();
        updateDTO.setName("Pet World Updated");
        updateDTO.setContactPerson("Suresh Kumar");
        updateDTO.setPhoneNumber("9871234560");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setAddressId(1);

        Supplier updatedEntity = new Supplier();
        updatedEntity.setSupplierId(1);
        updatedEntity.setName("Pet World Updated");
        updatedEntity.setContactPerson("Suresh Kumar");
        updatedEntity.setPhoneNumber("9871234560");
        updatedEntity.setEmail("updated@example.com");
        updatedEntity.setAddress(address);

        when(repo.findById(1)).thenReturn(Optional.of(supplier1));
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));
        when(repo.save(any(Supplier.class))).thenReturn(updatedEntity);

        SupplierDTO result = service.update(1, updateDTO);

        assertThat(result.getName()).isEqualTo("Pet World Updated");
        assertThat(result.getContactPerson()).isEqualTo("Suresh Kumar");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(repo).findById(1);
        verify(repo).save(any(Supplier.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        SupplierDTO dto = new SupplierDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Supplier not found");
    }

  

    @Test
    @DisplayName("getByNameDTO - match found - returns matching suppliers")
    void getByNameDTO_matchFound_returnsMatchingSuppliers() {
        when(repo.findByNameContainingIgnoreCase("Pet World")).thenReturn(List.of(supplier1));

        List<SupplierDTO> result = service.getByNameDTO("Pet World");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pet World Supplier");
    }

    @Test
    @DisplayName("getByNameDTO - no match - returns empty list")
    void getByNameDTO_noMatch_returnsEmpty() {
        when(repo.findByNameContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

        List<SupplierDTO> result = service.getByNameDTO("xyz");

        assertThat(result).isEmpty();
    }

  

    @Test
    @DisplayName("getByCityDTO - city exists - returns suppliers in that city")
    void getByCityDTO_cityExists_returnsMatchingSuppliers() {
        when(repo.findByAddress_CityIgnoreCase("Kolkata")).thenReturn(List.of(supplier1));

        List<SupplierDTO> result = service.getByCityDTO("Kolkata");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
    }

    @Test
    @DisplayName("getByCityDTO - no match - returns empty list")
    void getByCityDTO_noMatch_returnsEmpty() {
        when(repo.findByAddress_CityIgnoreCase("Unknown")).thenReturn(Collections.emptyList());

        List<SupplierDTO> result = service.getByCityDTO("Unknown");

        assertThat(result).isEmpty();
    }

   

    @Test
    @DisplayName("getByStateDTO - state exists - returns suppliers in that state")
    void getByStateDTO_stateExists_returnsMatchingSuppliers() {
        when(repo.findByAddress_StateIgnoreCase("West Bengal")).thenReturn(List.of(supplier1));

        List<SupplierDTO> result = service.getByStateDTO("West Bengal");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo("West Bengal");
    }

    @Test
    @DisplayName("getByStateDTO - no match - returns empty list")
    void getByStateDTO_noMatch_returnsEmpty() {
        when(repo.findByAddress_StateIgnoreCase("Unknown")).thenReturn(Collections.emptyList());

        List<SupplierDTO> result = service.getByStateDTO("Unknown");

        assertThat(result).isEmpty();
    }
}

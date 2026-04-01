package com.capgemini.training.servicetest;

import com.capgemini.training.entity.Address;
import com.capgemini.training.repository.AddressRepository;
import com.capgemini.training.service.AddressService;

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
class AddressServiceTest {

    @Mock
    private AddressRepository repo;

    @InjectMocks
    private AddressService service;

    private Address address1;
    private Address address2;

    @BeforeEach
    void setUp() {
        address1 = new Address();
        address1.setAddressId(1);
        address1.setStreet("123 Park Street");
        address1.setCity("Kolkata");
        address1.setState("West Bengal");
        address1.setZipCode("700001");

        address2 = new Address();
        address2.setAddressId(2);
        address2.setStreet("456 MG Road");
        address2.setCity("Delhi");
        address2.setState("Delhi");
        address2.setZipCode("110001");
    }

   

    @Test
    @DisplayName("getAll - returns all addresses")
    void getAll_returnsAllAddresses() {
        when(repo.findAll()).thenReturn(List.of(address1, address2));

        List<Address> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
        assertThat(result.get(1).getCity()).isEqualTo("Delhi");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAll - empty repo - returns empty list")
    void getAll_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<Address> result = service.getAll();

        assertThat(result).isEmpty();
    }

   

    @Test
    @DisplayName("getById - found - returns address")
    void getById_found_returnsAddress() {
        when(repo.findById(1)).thenReturn(Optional.of(address1));

        Address result = service.getById(1);

        assertThat(result).isNotNull();
        assertThat(result.getAddressId()).isEqualTo(1);
        assertThat(result.getCity()).isEqualTo("Kolkata");
        assertThat(result.getState()).isEqualTo("West Bengal");
        assertThat(result.getStreet()).isEqualTo("123 Park Street");
        assertThat(result.getZipCode()).isEqualTo("700001");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - returns null")
    void getById_notFound_returnsNull() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Address result = service.getById(99);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("add - valid address - saves and returns address")
    void add_validAddress_savesAndReturns() {
        Address newAddress = new Address();
        newAddress.setStreet("789 New Lane");
        newAddress.setCity("Mumbai");
        newAddress.setState("Maharashtra");
        newAddress.setZipCode("400001");

        Address savedAddress = new Address();
        savedAddress.setAddressId(3);
        savedAddress.setStreet("789 New Lane");
        savedAddress.setCity("Mumbai");
        savedAddress.setState("Maharashtra");
        savedAddress.setZipCode("400001");

        when(repo.save(any(Address.class))).thenReturn(savedAddress);

        Address result = service.add(newAddress);

        assertThat(result.getAddressId()).isEqualTo(3);
        assertThat(result.getCity()).isEqualTo("Mumbai");
        assertThat(result.getState()).isEqualTo("Maharashtra");
        verify(repo, times(1)).save(any(Address.class));
    }

   

    @Test
    @DisplayName("update - found - updates all fields and returns updated address")
    void update_found_updatesAndReturns() {
        Address updateData = new Address();
        updateData.setStreet("999 Updated Street");
        updateData.setCity("Chennai");
        updateData.setState("Tamil Nadu");
        updateData.setZipCode("600001");

        Address savedAddress = new Address();
        savedAddress.setAddressId(1);
        savedAddress.setStreet("999 Updated Street");
        savedAddress.setCity("Chennai");
        savedAddress.setState("Tamil Nadu");
        savedAddress.setZipCode("600001");

        when(repo.findById(1)).thenReturn(Optional.of(address1));
        when(repo.save(any(Address.class))).thenReturn(savedAddress);

        Address result = service.update(1, updateData);

        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo("Chennai");
        assertThat(result.getState()).isEqualTo("Tamil Nadu");
        assertThat(result.getStreet()).isEqualTo("999 Updated Street");
        assertThat(result.getZipCode()).isEqualTo("600001");
        verify(repo).findById(1);
        verify(repo).save(any(Address.class));
    }

    @Test
    @DisplayName("update - not found - returns null without saving")
    void update_notFound_returnsNull() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Address updateData = new Address();
        updateData.setCity("Anywhere");

        Address result = service.update(99, updateData);

        assertThat(result).isNull();
        verify(repo, never()).save(any());
    }
}

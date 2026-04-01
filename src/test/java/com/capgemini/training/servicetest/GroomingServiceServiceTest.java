package com.capgemini.training.servicetest;

import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.entity.GroomingService;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.repository.GroomingServiceRepository;
import com.capgemini.training.repository.PetRepository;
import com.capgemini.training.service.GroomingServiceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroomingServiceServiceTest {

    @Mock
    private GroomingServiceRepository repo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private GroomingServiceService service;

    private GroomingService svc1;
    private GroomingService svc2;

    @BeforeEach
    void setUp() {
        svc1 = new GroomingService();
        svc1.setServiceId(1);
        svc1.setName("Dog Bathing");
        svc1.setDescription("Refreshing bath for dogs");
        svc1.setPrice(new BigDecimal("1500.00"));
        svc1.setAvailable(true);

        svc2 = new GroomingService();
        svc2.setServiceId(2);
        svc2.setName("Cat Brushing");
        svc2.setDescription("Gentle brushing for cats");
        svc2.setPrice(new BigDecimal("800.00"));
        svc2.setAvailable(false);
    }

    

    @Test
    @DisplayName("getAllFiltered - no filters - returns all services")
    void getAllFiltered_noFilters_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(svc1, svc2));

        List<GroomingServiceDTO> result = service.getAllFiltered(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Dog Bathing");
        assertThat(result.get(1).getName()).isEqualTo("Cat Brushing");
    }

    @Test
    @DisplayName("getAllFiltered - minPrice filter - returns only expensive services")
    void getAllFiltered_minPrice_returnsExpensiveOnly() {
        when(repo.findAll()).thenReturn(List.of(svc1, svc2));

        List<GroomingServiceDTO> result = service.getAllFiltered(new BigDecimal("1000.00"), null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Dog Bathing");
    }

    @Test
    @DisplayName("getAllFiltered - maxPrice filter - returns only cheap services")
    void getAllFiltered_maxPrice_returnsCheapOnly() {
        when(repo.findAll()).thenReturn(List.of(svc1, svc2));

        List<GroomingServiceDTO> result = service.getAllFiltered(null, new BigDecimal("1000.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cat Brushing");
    }

    @Test
    @DisplayName("getAllFiltered - both price filters - returns services in range")
    void getAllFiltered_bothFilters_returnsInRange() {
        when(repo.findAll()).thenReturn(List.of(svc1, svc2));

        List<GroomingServiceDTO> result = service.getAllFiltered(
                new BigDecimal("700.00"), new BigDecimal("900.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cat Brushing");
    }

    @Test
    @DisplayName("getAllFiltered - empty repo - returns empty list")
    void getAllFiltered_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<GroomingServiceDTO> result = service.getAllFiltered(null, null);

        assertThat(result).isEmpty();
    }

   

    @Test
    @DisplayName("getById - found - returns correct DTO with all fields")
    void getById_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(svc1));

        GroomingServiceDTO result = service.getById(1);

        assertThat(result.getServiceId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Dog Bathing");
        assertThat(result.getDescription()).isEqualTo("Refreshing bath for dogs");
        assertThat(result.getPrice()).isEqualTo(1500.0);
        assertThat(result.isAvailable()).isTrue();
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service not found");
    }

   

    @Test
    @DisplayName("getAvailable - returns only available services")
    void getAvailable_returnsOnlyAvailableServices() {
        when(repo.findByAvailable(true)).thenReturn(List.of(svc1));

        List<GroomingServiceDTO> result = service.getAvailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
        assertThat(result.get(0).getName()).isEqualTo("Dog Bathing");
    }

    @Test
    @DisplayName("getAvailable - none available - returns empty list")
    void getAvailable_noneAvailable_returnsEmpty() {
        when(repo.findByAvailable(true)).thenReturn(Collections.emptyList());

        List<GroomingServiceDTO> result = service.getAvailable();

        assertThat(result).isEmpty();
    }

    

    @Test
    @DisplayName("getUnavailable - returns only unavailable services")
    void getUnavailable_returnsOnlyUnavailableServices() {
        when(repo.findByAvailable(false)).thenReturn(List.of(svc2));

        List<GroomingServiceDTO> result = service.getUnavailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isFalse();
        assertThat(result.get(0).getName()).isEqualTo("Cat Brushing");
    }

    

    @Test
    @DisplayName("add - valid DTO - saves and returns DTO")
    void add_validDTO_savesAndReturnsDTO() {
        GroomingServiceDTO inputDTO = new GroomingServiceDTO();
        inputDTO.setName("Horse Mane Styling");
        inputDTO.setDescription("Stylish mane grooming");
        inputDTO.setPrice(2000.0);
        inputDTO.setAvailable(true);

        GroomingService savedEntity = new GroomingService();
        savedEntity.setServiceId(3);
        savedEntity.setName("Horse Mane Styling");
        savedEntity.setDescription("Stylish mane grooming");
        savedEntity.setPrice(new BigDecimal("2000.00"));
        savedEntity.setAvailable(true);

        when(repo.save(any(GroomingService.class))).thenReturn(savedEntity);

        GroomingServiceDTO result = service.add(inputDTO);

        assertThat(result.getServiceId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Horse Mane Styling");
        assertThat(result.getPrice()).isEqualTo(2000.0);
        assertThat(result.isAvailable()).isTrue();
        verify(repo, times(1)).save(any(GroomingService.class));
    }

    

    @Test
    @DisplayName("update - found - updates all fields and returns updated DTO")
    void update_found_updatesAndReturnsDTO() {
        GroomingServiceDTO updateDTO = new GroomingServiceDTO();
        updateDTO.setName("Dog Bathing Premium");
        updateDTO.setDescription("Premium bath service");
        updateDTO.setPrice(2500.0);
        updateDTO.setAvailable(true);

        GroomingService updatedEntity = new GroomingService();
        updatedEntity.setServiceId(1);
        updatedEntity.setName("Dog Bathing Premium");
        updatedEntity.setDescription("Premium bath service");
        updatedEntity.setPrice(new BigDecimal("2500.00"));
        updatedEntity.setAvailable(true);

        when(repo.findById(1)).thenReturn(Optional.of(svc1));
        when(repo.save(any(GroomingService.class))).thenReturn(updatedEntity);

        GroomingServiceDTO result = service.update(1, updateDTO);

        assertThat(result.getName()).isEqualTo("Dog Bathing Premium");
        assertThat(result.getPrice()).isEqualTo(2500.0);
        verify(repo).findById(1);
        verify(repo).save(any(GroomingService.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        GroomingServiceDTO dto = new GroomingServiceDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service not found");
    }

    

    @Test
    @DisplayName("assignService - pet and service exist - assigns service to pet")
    void assignService_bothExist_assignsCorrectly() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setGroomingServices(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(svc1));

        service.assignService(1, 1);

        assertThat(pet.getGroomingServices()).hasSize(1);
        assertThat(pet.getGroomingServices().get(0).getServiceId()).isEqualTo(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignService - already assigned - does not add duplicate")
    void assignService_alreadyAssigned_doesNotAddDuplicate() {
        Pet pet = new Pet();
        pet.setPetId(1);
        List<GroomingService> services = new ArrayList<>();
        services.add(svc1);
        pet.setGroomingServices(services);

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(svc1));

        service.assignService(1, 1);

        assertThat(pet.getGroomingServices()).hasSize(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignService - pet not found - throws RuntimeException")
    void assignService_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignService(99, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }

    @Test
    @DisplayName("assignService - service not found - throws RuntimeException")
    void assignService_serviceNotFound_throwsException() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setGroomingServices(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignService(1, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service not found");
    }

    

    @Test
    @DisplayName("getServicesByPet - pet exists - returns all grooming service DTOs")
    void getServicesByPet_petExists_returnsServiceDTOs() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setGroomingServices(List.of(svc1, svc2));

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));

        List<GroomingServiceDTO> result = service.getServicesByPet(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Dog Bathing");
        assertThat(result.get(1).getName()).isEqualTo("Cat Brushing");
    }

    @Test
    @DisplayName("getServicesByPet - pet not found - throws RuntimeException")
    void getServicesByPet_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getServicesByPet(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }
}

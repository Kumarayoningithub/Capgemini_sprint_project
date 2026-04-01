package com.capgemini.training.servicetest;

import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.entity.Vaccination;
import com.capgemini.training.repository.PetRepository;
import com.capgemini.training.repository.VaccinationRepository;
import com.capgemini.training.service.VaccinationService;

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
class VaccinationServiceTest {

    @Mock
    private VaccinationRepository repo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private VaccinationService service;

    private Vaccination vacc1;
    private Vaccination vacc2;

    @BeforeEach
    void setUp() {
        vacc1 = new Vaccination();
        vacc1.setVaccinationId(1);
        vacc1.setName("Canine Parvovirus Vaccine");
        vacc1.setDescription("Prevents canine parvovirus");
        vacc1.setPrice(new BigDecimal("2500.00"));
        vacc1.setAvailable(true);

        vacc2 = new Vaccination();
        vacc2.setVaccinationId(2);
        vacc2.setName("Feline Distemper Vaccine");
        vacc2.setDescription("Protects against feline distemper");
        vacc2.setPrice(new BigDecimal("1800.00"));
        vacc2.setAvailable(false);
    }


    @Test
    @DisplayName("getAllFiltered - no filters - returns all vaccinations")
    void getAllFiltered_noFilters_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(vacc1, vacc2));

        List<VaccinationDTO> result = service.getAllFiltered(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Canine Parvovirus Vaccine");
        assertThat(result.get(1).getName()).isEqualTo("Feline Distemper Vaccine");
    }

    @Test
    @DisplayName("getAllFiltered - minPrice filter - returns only expensive vaccinations")
    void getAllFiltered_minPrice_returnsExpensiveOnly() {
        when(repo.findAll()).thenReturn(List.of(vacc1, vacc2));

        List<VaccinationDTO> result = service.getAllFiltered(new BigDecimal("2000.00"), null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Canine Parvovirus Vaccine");
    }

    @Test
    @DisplayName("getAllFiltered - maxPrice filter - returns only cheap vaccinations")
    void getAllFiltered_maxPrice_returnsCheapOnly() {
        when(repo.findAll()).thenReturn(List.of(vacc1, vacc2));

        List<VaccinationDTO> result = service.getAllFiltered(null, new BigDecimal("2000.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Feline Distemper Vaccine");
    }

    @Test
    @DisplayName("getAllFiltered - empty repo - returns empty list")
    void getAllFiltered_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<VaccinationDTO> result = service.getAllFiltered(null, null);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getById - found - returns correct DTO")
    void getById_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(vacc1));

        VaccinationDTO result = service.getById(1);

        assertThat(result.getVaccinationId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Canine Parvovirus Vaccine");
        assertThat(result.getDescription()).isEqualTo("Prevents canine parvovirus");
        assertThat(result.getPrice()).isEqualTo(2500.0);
        assertThat(result.isAvailable()).isTrue();
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vaccination not found");
    }


    @Test
    @DisplayName("getAvailable - returns only available vaccinations")
    void getAvailable_returnsOnlyAvailable() {
        when(repo.findByAvailable(true)).thenReturn(List.of(vacc1));

        List<VaccinationDTO> result = service.getAvailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
        assertThat(result.get(0).getName()).isEqualTo("Canine Parvovirus Vaccine");
    }


    @Test
    @DisplayName("getUnavailable - returns only unavailable vaccinations")
    void getUnavailable_returnsOnlyUnavailable() {
        when(repo.findByAvailable(false)).thenReturn(List.of(vacc2));

        List<VaccinationDTO> result = service.getUnavailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isFalse();
        assertThat(result.get(0).getName()).isEqualTo("Feline Distemper Vaccine");
    }


    @Test
    @DisplayName("add - valid DTO - saves and returns DTO")
    void add_validDTO_savesAndReturnsDTO() {
        VaccinationDTO inputDTO = new VaccinationDTO();
        inputDTO.setName("Equine Influenza Vaccine");
        inputDTO.setDescription("Protects horses from influenza");
        inputDTO.setPrice(3000.0);
        inputDTO.setAvailable(true);

        Vaccination savedEntity = new Vaccination();
        savedEntity.setVaccinationId(3);
        savedEntity.setName("Equine Influenza Vaccine");
        savedEntity.setDescription("Protects horses from influenza");
        savedEntity.setPrice(new BigDecimal("3000.00"));
        savedEntity.setAvailable(true);

        when(repo.save(any(Vaccination.class))).thenReturn(savedEntity);

        VaccinationDTO result = service.add(inputDTO);

        assertThat(result.getVaccinationId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Equine Influenza Vaccine");
        assertThat(result.getPrice()).isEqualTo(3000.0);
        verify(repo, times(1)).save(any(Vaccination.class));
    }


    @Test
    @DisplayName("update - found - updates all fields and returns updated DTO")
    void update_found_updatesAndReturnsDTO() {
        VaccinationDTO updateDTO = new VaccinationDTO();
        updateDTO.setName("Canine Parvovirus Booster");
        updateDTO.setDescription("Booster dose");
        updateDTO.setPrice(3000.0);
        updateDTO.setAvailable(true);

        Vaccination updatedEntity = new Vaccination();
        updatedEntity.setVaccinationId(1);
        updatedEntity.setName("Canine Parvovirus Booster");
        updatedEntity.setDescription("Booster dose");
        updatedEntity.setPrice(new BigDecimal("3000.00"));
        updatedEntity.setAvailable(true);

        when(repo.findById(1)).thenReturn(Optional.of(vacc1));
        when(repo.save(any(Vaccination.class))).thenReturn(updatedEntity);

        VaccinationDTO result = service.update(1, updateDTO);

        assertThat(result.getName()).isEqualTo("Canine Parvovirus Booster");
        assertThat(result.getPrice()).isEqualTo(3000.0);
        verify(repo).findById(1);
        verify(repo).save(any(Vaccination.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        VaccinationDTO dto = new VaccinationDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vaccination not found");
    }


    @Test
    @DisplayName("assignToPet - pet and vaccination exist - assigns correctly")
    void assignToPet_bothExist_assignsCorrectly() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setVaccinations(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(vacc1));

        service.assignToPet(1, 1);

        assertThat(pet.getVaccinations()).hasSize(1);
        assertThat(pet.getVaccinations().get(0).getVaccinationId()).isEqualTo(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignToPet - already assigned - does not add duplicate")
    void assignToPet_alreadyAssigned_doesNotAddDuplicate() {
        Pet pet = new Pet();
        pet.setPetId(1);
        List<Vaccination> vaccs = new ArrayList<>();
        vaccs.add(vacc1);
        pet.setVaccinations(vaccs);

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(vacc1));

        service.assignToPet(1, 1);

        assertThat(pet.getVaccinations()).hasSize(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignToPet - pet not found - throws RuntimeException")
    void assignToPet_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignToPet(99, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }

    @Test
    @DisplayName("assignToPet - vaccination not found - throws RuntimeException")
    void assignToPet_vaccinationNotFound_throwsException() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setVaccinations(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignToPet(1, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Vaccination not found");
    }


    @Test
    @DisplayName("getVaccinationsByPet - pet exists - returns vaccination DTOs")
    void getVaccinationsByPet_petExists_returnsVaccinationDTOs() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setVaccinations(List.of(vacc1, vacc2));

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));

        List<VaccinationDTO> result = service.getVaccinationsByPet(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Canine Parvovirus Vaccine");
        assertThat(result.get(1).getName()).isEqualTo("Feline Distemper Vaccine");
    }

    @Test
    @DisplayName("getVaccinationsByPet - pet not found - throws RuntimeException")
    void getVaccinationsByPet_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getVaccinationsByPet(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }
}
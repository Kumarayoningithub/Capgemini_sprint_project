

package com.capgemini.training.servicetest;

import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.entity.Employee;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.repository.EmployeeRepository;
import com.capgemini.training.repository.PetRepository;
import com.capgemini.training.service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repo;

    @Mock
    private PetRepository petRepo;

    @InjectMocks
    private EmployeeService service;

    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    void setUp() {
        emp1 = new Employee();
        emp1.setEmployeeId(1);
        emp1.setFirstName("John");
        emp1.setLastName("Smith");
        emp1.setPosition("Veterinarian");
        emp1.setHireDate(LocalDate.of(2022, 1, 1));
        emp1.setPhoneNumber("555-1234");
        emp1.setEmail("john.smith@example.com");
        emp1.setPets(new ArrayList<>());

        emp2 = new Employee();
        emp2.setEmployeeId(2);
        emp2.setFirstName("Emily");
        emp2.setLastName("Johnson");
        emp2.setPosition("Pet Groomer");
        emp2.setHireDate(LocalDate.of(2023, 6, 15));
        emp2.setPhoneNumber("555-5678");
        emp2.setEmail("emily.johnson@example.com");
        emp2.setPets(new ArrayList<>());
    }


    @Test
    @DisplayName("getAllFiltered - no filters - returns all employees")
    void getAllFiltered_noFilters_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(emp1, emp2));

        List<EmployeeDTO> result = service.getAllFiltered(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getFirstName()).isEqualTo("Emily");
    }

    @Test
    @DisplayName("getAllFiltered - minHireDate - filters out employees hired before date")
    void getAllFiltered_minHireDate_filtersEarlierEmployees() {
        when(repo.findAll()).thenReturn(List.of(emp1, emp2));

        List<EmployeeDTO> result = service.getAllFiltered(LocalDate.of(2023, 1, 1), null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Emily");
    }

    @Test
    @DisplayName("getAllFiltered - maxHireDate - filters out employees hired after date")
    void getAllFiltered_maxHireDate_filtersLaterEmployees() {
        when(repo.findAll()).thenReturn(List.of(emp1, emp2));

        List<EmployeeDTO> result = service.getAllFiltered(null, LocalDate.of(2022, 12, 31));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("getAllFiltered - both filters - returns only employees in range")
    void getAllFiltered_bothFilters_returnsOnlyInRange() {
        when(repo.findAll()).thenReturn(List.of(emp1, emp2));

        List<EmployeeDTO> result = service.getAllFiltered(
                LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("getAllFiltered - empty repo - returns empty list")
    void getAllFiltered_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<EmployeeDTO> result = service.getAllFiltered(null, null);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getById - found - returns correct DTO")
    void getById_found_returnsCorrectDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(emp1));

        EmployeeDTO result = service.getById(1);

        assertThat(result.getEmployeeId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getPosition()).isEqualTo("Veterinarian");
        assertThat(result.getHireDate()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(result.getPhoneNumber()).isEqualTo("555-1234");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found");
    }


    @Test
    @DisplayName("getByName - match found - returns matching employees")
    void getByName_matchFound_returnsMatching() {
        when(repo.findByFirstNameContainingIgnoreCase("john")).thenReturn(List.of(emp1));

        List<EmployeeDTO> result = service.getByName("john");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("getByName - no match - returns empty list")
    void getByName_noMatch_returnsEmpty() {
        when(repo.findByFirstNameContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

        List<EmployeeDTO> result = service.getByName("xyz");

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("getByPosition - match found - returns employees with that position")
    void getByPosition_matchFound_returnsMatching() {
        when(repo.findByPositionIgnoreCase("Veterinarian")).thenReturn(List.of(emp1));

        List<EmployeeDTO> result = service.getByPosition("Veterinarian");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPosition()).isEqualTo("Veterinarian");
    }

    @Test
    @DisplayName("getByPosition - no match - returns empty list")
    void getByPosition_noMatch_returnsEmpty() {
        when(repo.findByPositionIgnoreCase("Unknown")).thenReturn(Collections.emptyList());

        List<EmployeeDTO> result = service.getByPosition("Unknown");

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("add - valid DTO - saves and returns DTO")
    void add_validDTO_savesAndReturnsDTO() {
        EmployeeDTO inputDTO = new EmployeeDTO();
        inputDTO.setFirstName("David");
        inputDTO.setLastName("Williams");
        inputDTO.setPosition("Veterinary Assistant");
        inputDTO.setHireDate(LocalDate.of(2024, 1, 10));
        inputDTO.setPhoneNumber("555-9012");
        inputDTO.setEmail("david@example.com");

        Employee savedEntity = new Employee();
        savedEntity.setEmployeeId(3);
        savedEntity.setFirstName("David");
        savedEntity.setLastName("Williams");
        savedEntity.setPosition("Veterinary Assistant");
        savedEntity.setHireDate(LocalDate.of(2024, 1, 10));
        savedEntity.setPhoneNumber("555-9012");
        savedEntity.setEmail("david@example.com");

        when(repo.save(any(Employee.class))).thenReturn(savedEntity);

        EmployeeDTO result = service.add(inputDTO);

        assertThat(result.getEmployeeId()).isEqualTo(3);
        assertThat(result.getFirstName()).isEqualTo("David");
        assertThat(result.getPosition()).isEqualTo("Veterinary Assistant");
        verify(repo, times(1)).save(any(Employee.class));
    }


    @Test
    @DisplayName("update - found - updates all fields and returns updated DTO")
    void update_found_updatesAndReturnsDTO() {
        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Smith");
        updateDTO.setPosition("Senior Veterinarian");
        updateDTO.setHireDate(LocalDate.of(2022, 1, 1));
        updateDTO.setPhoneNumber("555-9999");
        updateDTO.setEmail("john.updated@example.com");

        Employee updatedEntity = new Employee();
        updatedEntity.setEmployeeId(1);
        updatedEntity.setFirstName("John");
        updatedEntity.setLastName("Smith");
        updatedEntity.setPosition("Senior Veterinarian");
        updatedEntity.setHireDate(LocalDate.of(2022, 1, 1));
        updatedEntity.setPhoneNumber("555-9999");
        updatedEntity.setEmail("john.updated@example.com");

        when(repo.findById(1)).thenReturn(Optional.of(emp1));
        when(repo.save(any(Employee.class))).thenReturn(updatedEntity);

        EmployeeDTO result = service.update(1, updateDTO);

        assertThat(result.getPosition()).isEqualTo("Senior Veterinarian");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("555-9999");
        verify(repo).findById(1);
        verify(repo).save(any(Employee.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        EmployeeDTO dto = new EmployeeDTO();
        dto.setFirstName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found");
    }


    @Test
    @DisplayName("assignEmployeeToPet - both exist - assigns without duplicate")
    void assignEmployeeToPet_bothExist_assignsCorrectly() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setEmployees(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(emp1));

        service.assignEmployeeToPet(1, 1);

        assertThat(pet.getEmployees()).hasSize(1);
        assertThat(pet.getEmployees().get(0).getEmployeeId()).isEqualTo(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignEmployeeToPet - already assigned - does not add duplicate")
    void assignEmployeeToPet_alreadyAssigned_doesNotAddDuplicate() {
        Pet pet = new Pet();
        pet.setPetId(1);
        List<Employee> emps = new ArrayList<>();
        emps.add(emp1);
        pet.setEmployees(emps);

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(1)).thenReturn(Optional.of(emp1));

        service.assignEmployeeToPet(1, 1);

        assertThat(pet.getEmployees()).hasSize(1);
        verify(petRepo).save(pet);
    }

    @Test
    @DisplayName("assignEmployeeToPet - pet not found - throws RuntimeException")
    void assignEmployeeToPet_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignEmployeeToPet(99, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }

    @Test
    @DisplayName("assignEmployeeToPet - employee not found - throws RuntimeException")
    void assignEmployeeToPet_employeeNotFound_throwsException() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setEmployees(new ArrayList<>());

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignEmployeeToPet(1, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee not found");
    }


    @Test
    @DisplayName("getEmployeesByPet - pet exists with employees - returns all employee DTOs")
    void getEmployeesByPet_petWithEmployees_returnsEmployeeDTOs() {
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setEmployees(List.of(emp1, emp2));

        when(petRepo.findById(1)).thenReturn(Optional.of(pet));

        List<EmployeeDTO> result = service.getEmployeesByPet(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(1).getFirstName()).isEqualTo("Emily");
    }

    @Test
    @DisplayName("getEmployeesByPet - pet not found - throws RuntimeException")
    void getEmployeesByPet_petNotFound_throwsException() {
        when(petRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEmployeesByPet(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }
}

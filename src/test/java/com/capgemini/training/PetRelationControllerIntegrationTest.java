package com.capgemini.training;

import com.capgemini.training.controller.PetRelationController;
import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.EmployeeService;
import com.capgemini.training.service.GroomingServiceService;
import com.capgemini.training.service.PetService;
import com.capgemini.training.service.VaccinationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PetRelationController.class)
@Import(GlobalExceptionHandler.class)
class PetRelationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;

    @MockitoBean
    private GroomingServiceService groomingService;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private VaccinationService vaccinationService;


    private PetDTO buildPetDto(int id, String name) {
        PetDTO dto = new PetDTO();
        dto.setPetId(id);
        dto.setName(name);
        dto.setBreed("Labrador");
        dto.setAge(2);
        dto.setPrice(new BigDecimal("25000.00"));
        return dto;
    }

    private GroomingServiceDTO buildServiceDto(int id, String name) {
        GroomingServiceDTO dto = new GroomingServiceDTO();
        dto.setServiceId(id);
        dto.setName(name);
        dto.setDescription("Bath and trim");
        dto.setPrice(2500.0);
        dto.setAvailable(true);
        return dto;
    }

    private VaccinationDTO buildVaccinationDto(int id, String name) {
        VaccinationDTO dto = new VaccinationDTO();
        dto.setVaccinationId(id);
        dto.setName(name);
        dto.setDescription("Core vaccine");
        dto.setPrice(1500.0);
        dto.setAvailable(true);
        return dto;
    }

    private EmployeeDTO buildEmployeeDto(int id, String firstName) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(id);
        dto.setFirstName(firstName);
        dto.setLastName("Das");
        dto.setPosition("Veterinarian");
        dto.setHireDate(LocalDate.of(2024, 1, 15));
        dto.setPhoneNumber("9876543210");
        dto.setEmail("emp@example.com");
        return dto;
    }

    private TransactionDTO buildTransactionDto(int id, String status) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(id);
        dto.setPetId(1);
        dto.setPetName("Bella");
        dto.setCustomerId(1);
        dto.setCustomerName("Alice Johnson");
        dto.setAmount(new BigDecimal("25000.00"));
        dto.setStatus(status);
        dto.setTransactionDate("2026-03-28");
        return dto;
    }


    @Test
    @DisplayName("POST /pets/{petId}/foods/{foodId} - success - returns 200 with pet DTO")
    void assignFood_success_returns200() throws Exception {
        when(petService.assignFood(1, 2)).thenReturn(buildPetDto(1, "Bella"));

        mockMvc.perform(post("/pets/1/foods/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(1))
                .andExpect(jsonPath("$.name").value("Bella"));

        verify(petService).assignFood(1, 2);
    }

    @Test
    @DisplayName("POST /pets/{petId}/foods/{foodId} - pet not found - returns 404")
    void assignFood_petNotFound_returns404() throws Exception {
        when(petService.assignFood(99, 1)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(post("/pets/99/foods/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /pets/{petId}/foods/{foodId} - food not found - returns 404")
    void assignFood_foodNotFound_returns404() throws Exception {
        when(petService.assignFood(1, 99)).thenThrow(new RuntimeException("Food not found"));

        mockMvc.perform(post("/pets/1/foods/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /pets/{petId}/suppliers/{supplierId} - success - returns 200 with pet DTO")
    void assignSupplier_success_returns200() throws Exception {
        when(petService.assignSupplier(1, 3)).thenReturn(buildPetDto(1, "Bella"));

        mockMvc.perform(post("/pets/1/suppliers/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bella"));

        verify(petService).assignSupplier(1, 3);
    }

    @Test
    @DisplayName("POST /pets/{petId}/suppliers/{supplierId} - supplier not found - returns 404")
    void assignSupplier_notFound_returns404() throws Exception {
        when(petService.assignSupplier(1, 99)).thenThrow(new RuntimeException("Supplier not found"));

        mockMvc.perform(post("/pets/1/suppliers/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /pets/{petId}/services/{serviceId} - success - returns 200 with message")
    void assignService_success_returns200() throws Exception {
        doNothing().when(groomingService).assignService(1, 2);

        mockMvc.perform(post("/pets/1/services/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service assigned"));

        verify(groomingService).assignService(1, 2);
    }

    @Test
    @DisplayName("POST /pets/{petId}/services/{serviceId} - service not found - returns 404")
    void assignService_notFound_returns404() throws Exception {
        doThrow(new RuntimeException("Service not found")).when(groomingService).assignService(1, 99);

        mockMvc.perform(post("/pets/1/services/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /pets/{petId}/services - returns 200 with service list")
    void getServicesByPet_returns200() throws Exception {
        when(groomingService.getServicesByPet(1))
                .thenReturn(List.of(buildServiceDto(1, "Full Grooming")));

        mockMvc.perform(get("/pets/1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Full Grooming"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET /pets/{petId}/services - no services - returns 200 empty list")
    void getServicesByPet_empty_returns200() throws Exception {
        when(groomingService.getServicesByPet(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/1/services"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /pets/{petId}/services - pet not found - returns 404")
    void getServicesByPet_petNotFound_returns404() throws Exception {
        when(groomingService.getServicesByPet(99)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(get("/pets/99/services"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /pets/{petId}/vaccinations/{vaccinationId} - success - returns 200 with message")
    void assignVaccination_success_returns200() throws Exception {
        doNothing().when(vaccinationService).assignToPet(1, 2);

        mockMvc.perform(post("/pets/1/vaccinations/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Vaccination assigned to pet"));

        verify(vaccinationService).assignToPet(1, 2);
    }

    @Test
    @DisplayName("POST /pets/{petId}/vaccinations/{vaccinationId} - vaccination not found - returns 404")
    void assignVaccination_notFound_returns404() throws Exception {
        doThrow(new RuntimeException("Vaccination not found")).when(vaccinationService).assignToPet(1, 99);

        mockMvc.perform(post("/pets/1/vaccinations/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /pets/{petId}/vaccinations - returns 200 with vaccination list")
    void getVaccinationsByPet_returns200() throws Exception {
        when(vaccinationService.getVaccinationsByPet(1))
                .thenReturn(List.of(buildVaccinationDto(1, "Rabies Vaccine")));

        mockMvc.perform(get("/pets/1/vaccinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Rabies Vaccine"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET /pets/{petId}/vaccinations - pet not found - returns 404")
    void getVaccinationsByPet_petNotFound_returns404() throws Exception {
        when(vaccinationService.getVaccinationsByPet(99)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(get("/pets/99/vaccinations"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /pets/{petId}/employees/{employeeId} - success - returns 200 with message")
    void assignEmployee_success_returns200() throws Exception {
        doNothing().when(employeeService).assignEmployeeToPet(1, 2);

        mockMvc.perform(post("/pets/1/employees/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee assigned"));

        verify(employeeService).assignEmployeeToPet(1, 2);
    }

    @Test
    @DisplayName("POST /pets/{petId}/employees/{employeeId} - employee not found - returns 404")
    void assignEmployee_notFound_returns404() throws Exception {
        doThrow(new RuntimeException("Employee not found")).when(employeeService).assignEmployeeToPet(1, 99);

        mockMvc.perform(post("/pets/1/employees/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /pets/{petId}/employees - returns 200 with employee list")
    void getEmployeesByPet_returns200() throws Exception {
        when(employeeService.getEmployeesByPet(1))
                .thenReturn(List.of(buildEmployeeDto(1, "Rohit")));

        mockMvc.perform(get("/pets/1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Rohit"))
                .andExpect(jsonPath("$[0].position").value("Veterinarian"));
    }

    @Test
    @DisplayName("GET /pets/{petId}/employees - no employees - returns 200 empty list")
    void getEmployeesByPet_empty_returns200() throws Exception {
        when(employeeService.getEmployeesByPet(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/1/employees"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /pets/{petId}/employees - pet not found - returns 404")
    void getEmployeesByPet_petNotFound_returns404() throws Exception {
        when(employeeService.getEmployeesByPet(99)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(get("/pets/99/employees"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /pets/{petId}/transactions - returns 200 with transaction list")
    void getTransactionsByPet_returns200() throws Exception {
        when(petService.getTransactionHistory(1))
                .thenReturn(List.of(buildTransactionDto(1, "Success")));

        mockMvc.perform(get("/pets/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("Success"))
                .andExpect(jsonPath("$[0].customerName").value("Alice Johnson"))
                .andExpect(jsonPath("$[0].petName").value("Bella"));
    }

    @Test
    @DisplayName("GET /pets/{petId}/transactions - no transactions - returns 200 empty list")
    void getTransactionsByPet_empty_returns200() throws Exception {
        when(petService.getTransactionHistory(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /pets/{petId}/transactions - pet not found - returns 404")
    void getTransactionsByPet_petNotFound_returns404() throws Exception {
        when(petService.getTransactionHistory(99)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(get("/pets/99/transactions"))
                .andExpect(status().isNotFound());
    }
}
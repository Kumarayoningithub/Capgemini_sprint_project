package com.capgemini.training;

import com.capgemini.training.controller.EmployeeController;
import com.capgemini.training.dto.EmployeeDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    private EmployeeDTO buildEmployee(int id, String first, String last, String position,
                                      LocalDate hireDate, String phone, String email) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(id);
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setPosition(position);
        dto.setHireDate(hireDate);
        dto.setPhoneNumber(phone);
        dto.setEmail(email);
        return dto;
    }

    @Test
    @DisplayName("GET /employees returns 200 with Priya and Raj")
    void getAll_returns200() throws Exception {
        when(employeeService.getAllFiltered(isNull(), isNull())).thenReturn(List.of(
                buildEmployee(1, "Priya", "Sen", "Veterinarian", LocalDate.of(2024, 6, 1), "9876500001", "priya@example.com"),
                buildEmployee(2, "Raj", "Malhotra", "Groomer", LocalDate.of(2025, 1, 10), "9876500002", "raj@example.com")));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Priya"))
                .andExpect(jsonPath("$[1].position").value("Groomer"));
    }

    @Test
    @DisplayName("GET /employees empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(employeeService.getAllFiltered(isNull(), isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /employees with hire date range returns filtered list")
    void getAll_withDateFilters_returns200() throws Exception {
        when(employeeService.getAllFiltered(eq(LocalDate.of(2024, 1, 1)), eq(LocalDate.of(2026, 12, 31))))
                .thenReturn(List.of(buildEmployee(1, "Ayon", "Das", "Veterinarian", LocalDate.of(2024, 1, 15), "9876541230", "ayon@example.com")));

        mockMvc.perform(get("/employees")
                        .param("minHireDate", "2024-01-01")
                        .param("maxHireDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ayon"));
    }

    @Test
    @DisplayName("GET /employees/name/{name} returns 200")
    void getByName_returns200() throws Exception {
        when(employeeService.getByName("Priya")).thenReturn(List.of(
                buildEmployee(1, "Priya", "Sen", "Veterinarian", LocalDate.of(2024, 6, 1), "9876500001", "priya@example.com")));

        mockMvc.perform(get("/employees/name/{name}", "Priya"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].lastName").value("Sen"));
    }

    @Test
    @DisplayName("GET /employees/position/{position} returns 200")
    void getByPosition_returns200() throws Exception {
        when(employeeService.getByPosition("Veterinarian")).thenReturn(List.of(
                buildEmployee(1, "Priya", "Sen", "Veterinarian", LocalDate.of(2024, 6, 1), "9876500001", "priya@example.com")));

        mockMvc.perform(get("/employees/position/{position}", "Veterinarian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value("Veterinarian"));
    }

    @Test
    @DisplayName("GET /employees/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(employeeService.getById(1)).thenReturn(
                buildEmployee(1, "Rohit", "Das", "Veterinarian", LocalDate.of(2024, 1, 15), "9876541230", "rohit@example.com"));

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.email").value("rohit@example.com"));
    }

    @Test
    @DisplayName("GET /employees/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(employeeService.getById(99)).thenThrow(new RuntimeException("Employee not found"));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /employees/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(employeeService.add(any(EmployeeDTO.class))).thenReturn(
                buildEmployee(5, "Ankit", "Roy", "Pet Groomer", LocalDate.of(2025, 2, 20), "9988123456", "ankit@example.com"));

        mockMvc.perform(post("/employees/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildEmployee(0, "Ankit", "Roy", "Pet Groomer", LocalDate.of(2025, 2, 20), "9988123456", "ankit@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ankit"))
                .andExpect(jsonPath("$.position").value("Pet Groomer"));
    }

    @Test
    @DisplayName("PUT /employees/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(employeeService.update(eq(1), any(EmployeeDTO.class))).thenReturn(
                buildEmployee(1, "Ankit", "Roy", "Pet Groomer", LocalDate.of(2025, 2, 20), "9988123456", "ankit@example.com"));

        mockMvc.perform(put("/employees/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ankit","lastName":"Roy","position":"Pet Groomer","hireDate":"2025-02-20","phoneNumber":"9988123456","email":"ankit@example.com","address":{"addressId":1}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Roy"));
    }

    @Test
    @DisplayName("PUT /employees/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(employeeService.update(eq(99), any(EmployeeDTO.class)))
                .thenThrow(new RuntimeException("Employee not found for update"));

        mockMvc.perform(put("/employees/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"X\",\"lastName\":\"Y\",\"position\":\"Z\",\"hireDate\":\"2024-01-01\",\"phoneNumber\":\"1\",\"email\":\"e@e.com\"}"))
                .andExpect(status().isNotFound());
    }
}

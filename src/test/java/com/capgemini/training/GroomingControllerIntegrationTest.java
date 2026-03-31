package com.capgemini.training;

import com.capgemini.training.controller.GroomingController;
import com.capgemini.training.dto.GroomingServiceDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.GroomingServiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
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

@WebMvcTest(controllers = GroomingController.class)
@Import(GlobalExceptionHandler.class)
class GroomingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroomingServiceService groomingServiceService;

    private GroomingServiceDTO buildGroomingDto(int id, String name, String desc, double price, boolean available) {
        GroomingServiceDTO dto = new GroomingServiceDTO();
        dto.setServiceId(id);
        dto.setName(name);
        dto.setDescription(desc);
        dto.setPrice(price);
        dto.setAvailable(available);
        return dto;
    }

    @Test
    @DisplayName("GET /services returns 200 with list")
    void getAll_returns200() throws Exception {
        when(groomingServiceService.getAllFiltered(isNull(), isNull())).thenReturn(List.of(
                buildGroomingDto(1, "Full Grooming", "Bath and cut", 2500.0, true)));

        mockMvc.perform(get("/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Full Grooming"));
    }

    @Test
    @DisplayName("GET /services empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(groomingServiceService.getAllFiltered(isNull(), isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/services"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /services with minPrice and maxPrice returns filtered list")
    void getAll_withPriceFilters_returns200() throws Exception {
        when(groomingServiceService.getAllFiltered(
                any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(buildGroomingDto(1, "Spa", "Deluxe", 3000.0, true)));

        mockMvc.perform(get("/services")
                        .param("minPrice", "1000")
                        .param("maxPrice", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].price").value(3000.0));
    }

    @Test
    @DisplayName("GET /services/available returns 200")
    void getAvailable_returns200() throws Exception {
        when(groomingServiceService.getAvailable()).thenReturn(List.of(
                buildGroomingDto(1, "Basic", "Nails", 800.0, true)));

        mockMvc.perform(get("/services/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET /services/unavailable returns 200")
    void getUnavailable_returns200() throws Exception {
        when(groomingServiceService.getUnavailable()).thenReturn(List.of(
                buildGroomingDto(2, "Premium", "Waitlist", 5000.0, false)));

        mockMvc.perform(get("/services/unavailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(false));
    }

    @Test
    @DisplayName("GET /services/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(groomingServiceService.getById(1)).thenReturn(
                buildGroomingDto(1, "Full Grooming", "Bath", 2500.0, true));

        mockMvc.perform(get("/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value(1))
                .andExpect(jsonPath("$.description").value("Bath"));
    }

    @Test
    @DisplayName("GET /services/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(groomingServiceService.getById(99)).thenThrow(new RuntimeException("Service not found"));

        mockMvc.perform(get("/services/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /services/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(groomingServiceService.add(any(GroomingServiceDTO.class))).thenReturn(
                buildGroomingDto(3, "Puppy Trim", "Light trim", 1200.0, true));

        mockMvc.perform(post("/services/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildGroomingDto(0, "Puppy Trim", "Light trim", 1200.0, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Puppy Trim"));
    }

    @Test
    @DisplayName("PUT /services/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(groomingServiceService.update(eq(1), any(GroomingServiceDTO.class))).thenReturn(
                buildGroomingDto(1, "Basic Grooming", "Bath only", 1500.0, true));

        mockMvc.perform(put("/services/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Basic Grooming\",\"description\":\"Bath only\",\"price\":1500.0,\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Basic Grooming"));
    }

    @Test
    @DisplayName("PUT /services/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(groomingServiceService.update(eq(99), any(GroomingServiceDTO.class)))
                .thenThrow(new RuntimeException("Service not found for update"));

        mockMvc.perform(put("/services/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"description\":\"Y\",\"price\":1.0,\"available\":false}"))
                .andExpect(status().isNotFound());
    }
}
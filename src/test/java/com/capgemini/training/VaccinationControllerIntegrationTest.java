package com.capgemini.training;

import com.capgemini.training.controller.VaccinationController;
import com.capgemini.training.dto.VaccinationDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.VaccinationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VaccinationController.class)
@Import(GlobalExceptionHandler.class)
class VaccinationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VaccinationService vaccinationService;

    private VaccinationDTO buildVaccination(int id, String name, String desc, double price, boolean available) {
        VaccinationDTO dto = new VaccinationDTO();
        dto.setVaccinationId(id);
        dto.setName(name);
        dto.setDescription(desc);
        dto.setPrice(price);
        dto.setAvailable(available);
        return dto;
    }

  

    @Test
    @DisplayName("GET /vaccinations returns 200")
    void getAll_returns200() throws Exception {
        when(vaccinationService.getAllFiltered(any(), any())).thenReturn(List.of(
                buildVaccination(1, "Rabies Vaccine", "Annual shot", 1500.0, true)));

        mockMvc.perform(get("/vaccinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Rabies Vaccine"));
    }

    @Test
    @DisplayName("GET /vaccinations empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(vaccinationService.getAllFiltered(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/vaccinations"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /vaccinations with minPrice and maxPrice returns filtered list")
    void getAll_withPriceFilters_returns200() throws Exception {
        when(vaccinationService.getAllFiltered(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(buildVaccination(1, "Distemper Vaccine", "Core vaccine", 1800.0, true)));

        mockMvc.perform(get("/vaccinations")
                        .param("minPrice", "500")
                        .param("maxPrice", "3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

   

    @Test
    @DisplayName("GET /vaccinations/available returns 200")
    void getAvailable_returns200() throws Exception {
        when(vaccinationService.getAvailable()).thenReturn(List.of(
                buildVaccination(1, "Rabies Vaccine", "Annual", 1500.0, true)));

        mockMvc.perform(get("/vaccinations/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET /vaccinations/unavailable returns 200")
    void getUnavailable_returns200() throws Exception {
        when(vaccinationService.getUnavailable()).thenReturn(List.of(
                buildVaccination(2, "Imported Serum", "Out of stock", 5000.0, false)));

        mockMvc.perform(get("/vaccinations/unavailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(false));
    }

  

    @Test
    @DisplayName("GET /vaccinations/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(vaccinationService.getById(1)).thenReturn(
                buildVaccination(1, "Rabies Vaccine", "Protection against rabies", 1500.0, true));

        mockMvc.perform(get("/vaccinations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vaccinationId").value(1))
                .andExpect(jsonPath("$.price").value(1500.0));
    }

    @Test
    @DisplayName("GET /vaccinations/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(vaccinationService.getById(99)).thenThrow(new RuntimeException("Vaccination not found"));

        mockMvc.perform(get("/vaccinations/99"))
                .andExpect(status().isNotFound());
    }

   

    @Test
    @DisplayName("POST /vaccinations/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(vaccinationService.add(any(VaccinationDTO.class))).thenReturn(
                buildVaccination(3, "Parvo Vaccine", "Puppy series", 2000.0, true));

        mockMvc.perform(post("/vaccinations/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildVaccination(0, "Parvo Vaccine", "Puppy series", 2000.0, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Parvo Vaccine"));
    }



    @Test
    @DisplayName("PUT /vaccinations/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(vaccinationService.update(eq(1), any(VaccinationDTO.class))).thenReturn(
                buildVaccination(1, "Distemper Vaccine", "Protection against distemper", 1800.0, true));

        mockMvc.perform(put("/vaccinations/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Distemper Vaccine\",\"description\":\"Protection against distemper\",\"price\":1800.0,\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Distemper Vaccine"));
    }

    @Test
    @DisplayName("PUT /vaccinations/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(vaccinationService.update(eq(99), any(VaccinationDTO.class)))
                .thenThrow(new RuntimeException("Vaccination not found for update"));

        mockMvc.perform(put("/vaccinations/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"description\":\"Y\",\"price\":1.0,\"available\":false}"))
                .andExpect(status().isNotFound());
    }
}


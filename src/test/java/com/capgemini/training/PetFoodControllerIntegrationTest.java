package com.capgemini.training;

import com.capgemini.training.controller.PetFoodController;
import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.PetFoodService;
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

@WebMvcTest(controllers = PetFoodController.class)
@Import(GlobalExceptionHandler.class)
class PetFoodControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetFoodService petFoodService;

    private PetFoodDTO buildPetFood(int id, String name, String brand, String type, int qty, BigDecimal price) {
        PetFoodDTO dto = new PetFoodDTO();
        dto.setFoodId(id);
        dto.setName(name);
        dto.setBrand(brand);
        dto.setType(type);
        dto.setQuantity(qty);
        dto.setPrice(price);
        return dto;
    }

    @Test
    @DisplayName("GET /pet_foods returns 200 with Pedigree and Whiskas")
    void getAll_returns200() throws Exception {
        when(petFoodService.getAllFiltered(any(), any(), any(), any())).thenReturn(List.of(
                buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00")),
                buildPetFood(2, "Whiskas Pouch", "Whiskas", "Wet Food", 30, new BigDecimal("900.00"))));

        mockMvc.perform(get("/pet_foods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].brand").value("Pedigree"));
    }

    @Test
    @DisplayName("GET /pet_foods empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(petFoodService.getAllFiltered(any(), any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pet_foods"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /pet_foods with price and quantity filters returns 200")
    void getAll_withFilters_returns200() throws Exception {
        when(petFoodService.getAllFiltered(any(BigDecimal.class), any(BigDecimal.class), any(), any()))
                .thenReturn(List.of(buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00"))));

        mockMvc.perform(get("/pet_foods")
                        .param("minPrice", "500")
                        .param("maxPrice", "2000")
                        .param("minQuantity", "10")
                        .param("maxQuantity", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(50));
    }

    @Test
    @DisplayName("GET /pet_foods/search?name= returns 200")
    void searchByName_returns200() throws Exception {
        when(petFoodService.searchByNameDTO("Pedigree")).thenReturn(List.of(
                buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00"))));

        mockMvc.perform(get("/pet_foods/search").param("name", "Pedigree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pedigree Adult"));
    }

    @Test
    @DisplayName("GET /pet_foods/food_type/{type} returns 200")
    void getByType_returns200() throws Exception {
        when(petFoodService.getByTypeDTO("Dry Food")).thenReturn(List.of(
                buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00"))));

        mockMvc.perform(get("/pet_foods/food_type/{type}", "Dry Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("Dry Food"));
    }

    @Test
    @DisplayName("GET /pet_foods/brand/{brand} returns 200")
    void getByBrand_returns200() throws Exception {
        when(petFoodService.getByBrandDTO("Pedigree")).thenReturn(List.of(
                buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00"))));

        mockMvc.perform(get("/pet_foods/brand/{brand}", "Pedigree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Pedigree"));
    }

    @Test
    @DisplayName("GET /pet_foods/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(petFoodService.getByIdDTO(1)).thenReturn(
                buildPetFood(1, "Pedigree Adult", "Pedigree", "Dry Food", 50, new BigDecimal("1200.00")));

        mockMvc.perform(get("/pet_foods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodId").value(1))
                .andExpect(jsonPath("$.price").value(1200.0));
    }

    @Test
    @DisplayName("GET /pet_foods/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(petFoodService.getByIdDTO(99)).thenThrow(new RuntimeException("Pet food not found"));

        mockMvc.perform(get("/pet_foods/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /pet_foods/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(petFoodService.add(any(PetFoodDTO.class))).thenReturn(
                buildPetFood(3, "Royal Canin Puppy", "Royal Canin", "Dry Food", 20, new BigDecimal("2500.00")));

        mockMvc.perform(post("/pet_foods/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildPetFood(0, "Royal Canin Puppy", "Royal Canin", "Dry Food", 20, new BigDecimal("2500.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Royal Canin Puppy"));
    }


    @Test
    @DisplayName("PUT /pet_foods/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(petFoodService.update(eq(1), any(PetFoodDTO.class))).thenReturn(
                buildPetFood(1, "Whiskas Cat Food", "Whiskas", "Wet Food", 30, new BigDecimal("900.00")));

        mockMvc.perform(put("/pet_foods/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Whiskas Cat Food\",\"brand\":\"Whiskas\",\"type\":\"Wet Food\",\"quantity\":30,\"price\":900.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Whiskas"));
    }

    @Test
    @DisplayName("PUT /pet_foods/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(petFoodService.update(eq(99), any(PetFoodDTO.class)))
                .thenThrow(new RuntimeException("Pet food not found for update"));

        mockMvc.perform(put("/pet_foods/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"brand\":\"Y\",\"type\":\"Z\",\"quantity\":1,\"price\":1}"))
                .andExpect(status().isNotFound());
    }
}

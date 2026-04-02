package com.capgemini.training;

import com.capgemini.training.controller.PetController;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.PetService;
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

@WebMvcTest(controllers = PetController.class)
@Import(GlobalExceptionHandler.class)
class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PetService petService;

    private PetDTO buildPetDto(int id, String name, String breed, int age, BigDecimal price, String categoryName) {
        PetDTO dto = new PetDTO();
        dto.setPetId(id);
        dto.setName(name);
        dto.setBreed(breed);
        dto.setAge(age);
        dto.setPrice(price);
        dto.setCategoryName(categoryName);
        return dto;
    }

    private PetDTO buildPetAddRequestBody() {
        PetDTO dto = buildPetDto(0, "Tommy", "Golden Retriever", 2, new BigDecimal("45000.00"), "Dogs");
        dto.setDescription("Friendly");
        dto.setImageUrl("https://example.com/dog.jpg");

        PetDTO.CategoryRef cat = new PetDTO.CategoryRef();
        cat.setCategoryId(1);
        dto.setCategory(cat);

        return dto;
    }

    private PetFoodDTO buildPetFood(int id, String name, String brand) {
        PetFoodDTO f = new PetFoodDTO();
        f.setFoodId(id);
        f.setName(name);
        f.setBrand(brand);
        f.setType("Dry Food");
        f.setQuantity(10);
        f.setPrice(new BigDecimal("500"));
        return f;
    }

    private SupplierDTO buildSupplier(int id, String name, String city) {
        SupplierDTO s = new SupplierDTO();
        s.setSupplierId(id);
        s.setName(name);
        s.setCity(city);
        s.setState("West Bengal");
        s.setContactPerson("Rahul");
        s.setPhoneNumber("9876543210");
        s.setEmail("supplier@test.com");
        return s;
    }

    private TransactionDTO buildTransaction(int tid, String status) {
        TransactionDTO t = new TransactionDTO();
        t.setTransactionId(tid);
        t.setCustomerId(1);
        t.setCustomerName("Ayon Saha");
        t.setPetId(1);
        t.setPetName("Tommy");
        t.setAmount(new BigDecimal("45000.00"));
        t.setStatus(status);
        t.setTransactionDate("2026-03-28");
        return t;
    }

    @Test
    @DisplayName("GET /pets returns 200 with pets in Kolkata customer context")
    void getPets_returns200() throws Exception {
        when(petService.getPetsFiltered(isNull(), isNull(), isNull(), isNull())).thenReturn(List.of(
                buildPetDto(1, "Tommy", "Golden Retriever", 2, new BigDecimal("45000.00"), "Dogs")));

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Tommy"))
                .andExpect(jsonPath("$[0].categoryName").value("Dogs"));
    }

    @Test
    @DisplayName("GET /pets empty returns 200")
    void getPets_empty_returns200() throws Exception {
        when(petService.getPetsFiltered(isNull(), isNull(), isNull(), isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /pets with price and age filters returns 200")
    void getPets_withFilters_returns200() throws Exception {
        when(petService.getPetsFiltered(any(BigDecimal.class), any(BigDecimal.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(buildPetDto(1, "Bruno", "German Shepherd", 3, new BigDecimal("55000.00"), "Dogs")));

        mockMvc.perform(get("/pets")
                        .param("minPrice", "10000")
                        .param("maxPrice", "60000")
                        .param("minAge", "1")
                        .param("maxAge", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].breed").value("German Shepherd"));
    }

    @Test
    @DisplayName("GET /pets/food_info/{petId} returns 200")
    void getFoodInfo_returns200() throws Exception {
        when(petService.getPetFoods(1)).thenReturn(List.of(buildPetFood(1, "Pedigree Adult", "Pedigree")));

        mockMvc.perform(get("/pets/food_info/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Pedigree"));
    }

    @Test
    @DisplayName("GET /pets/suppliers/{petId} returns 200")
    void getSuppliers_returns200() throws Exception {
        when(petService.getSuppliersDTO(1)).thenReturn(List.of(buildSupplier(1, "Pet World Supplier", "Kolkata")));

        mockMvc.perform(get("/pets/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Kolkata"))
                .andExpect(jsonPath("$[0].state").value("West Bengal"));
    }

    
    @Test
    @DisplayName("POST /pets/add valid returns 200")
    void addPet_valid_returns200() throws Exception {
        when(petService.addPet(any(PetDTO.class))).thenReturn(
                buildPetDto(1, "Tommy", "Golden Retriever", 2, new BigDecimal("45000.00"), "Dogs"));

        mockMvc.perform(post("/pets/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildPetAddRequestBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(1))
                .andExpect(jsonPath("$.name").value("Tommy"));
    }

    @Test
    @DisplayName("PUT /pets/update/{petId} found returns 200")
    void updatePet_found_returns200() throws Exception {
        when(petService.updatePet(eq(1), any(PetDTO.class))).thenReturn(
                buildPetDto(1, "Bruno", "German Shepherd", 3, new BigDecimal("55000.00"), "Dogs"));

        mockMvc.perform(put("/pets/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Bruno","breed":"German Shepherd","age":3,"price":55000.00,"description":"Loyal","imageUrl":"https://example.com/dog2.jpg","category":{"categoryId":1}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bruno"));
    }

    @Test
    @DisplayName("PUT /pets/update/{petId} not found returns 404")
    void updatePet_notFound_returns404() throws Exception {
        when(petService.updatePet(eq(99), any(PetDTO.class)))
                .thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(put("/pets/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"breed\":\"Y\",\"age\":1,\"price\":1,\"category\":{\"categoryId\":1}}"))
                .andExpect(status().isNotFound());
    }
}
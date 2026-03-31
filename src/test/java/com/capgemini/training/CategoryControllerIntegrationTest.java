package com.capgemini.training;

import com.capgemini.training.controller.CategoryController;
import com.capgemini.training.dto.CategoryDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

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

@WebMvcTest(controllers = CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryDTO buildCategoryDto(int id, String name) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(id);
        dto.setName(name);
        return dto;
    }

    @Test
    @DisplayName("GET /categories returns 200 with Dogs and Cats")
    void getAll_returns200WithList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(
                buildCategoryDto(1, "Dogs"),
                buildCategoryDto(2, "Cats")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Dogs"))
                .andExpect(jsonPath("$[1].name").value("Cats"));
    }

    @Test
    @DisplayName("GET /categories empty returns 200 and []")
    void getAll_empty_returns200() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /categories/name/{name} when found returns 200")
    void getByName_found_returns200() throws Exception {
        when(categoryService.getByName("Dogs")).thenReturn(buildCategoryDto(1, "Dogs"));

        mockMvc.perform(get("/categories/name/{name}", "Dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.name").value("Dogs"));
    }

    @Test
    @DisplayName("GET /categories/name/{name} when not found returns 404")
    void getByName_notFound_returns404() throws Exception {
        when(categoryService.getByName("Birds")).thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(get("/categories/name/{name}", "Birds"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /categories/{id} when found returns 200")
    void getById_found_returns200() throws Exception {
        when(categoryService.getById(1)).thenReturn(buildCategoryDto(1, "Dogs"));

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1));
    }

    @Test
    @DisplayName("GET /categories/{id} when not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(categoryService.getById(99)).thenThrow(new RuntimeException("Category id not found"));

        mockMvc.perform(get("/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /categories/add valid body returns 200 with saved fields")
    void add_valid_returns200() throws Exception {
        when(categoryService.add(any(CategoryDTO.class))).thenReturn(buildCategoryDto(5, "Exotic"));

        mockMvc.perform(post("/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCategoryDto(0, "Exotic"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(5))
                .andExpect(jsonPath("$.name").value("Exotic"));
    }

    @Test
    @DisplayName("PUT /categories/update/{id} when found returns 200")
    void update_found_returns200() throws Exception {
        when(categoryService.update(eq(1), any(CategoryDTO.class))).thenReturn(buildCategoryDto(1, "Cats"));

        mockMvc.perform(put("/categories/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Cats\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cats"));
    }

    @Test
    @DisplayName("PUT /categories/update/{id} when not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(categoryService.update(eq(99), any(CategoryDTO.class)))
                .thenThrow(new RuntimeException("Category not found for update"));

        mockMvc.perform(put("/categories/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isNotFound());
    }
}

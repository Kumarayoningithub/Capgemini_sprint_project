package com.capgemini.training.servicetest;

import com.capgemini.training.dto.CategoryDTO;
import com.capgemini.training.entity.PetCategory;
import com.capgemini.training.repository.PetCategoryRepository;
import com.capgemini.training.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private PetCategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    private PetCategory cats;
    private PetCategory dogs;

    @BeforeEach
    void setUp() {
        cats = new PetCategory(1, "Cats");
        dogs = new PetCategory(2, "Dogs");
    }

  

    @Test
    @DisplayName("getAllCategories - returns all categories as DTOs")
    void getAllCategories_returnsAllCategories() {
        when(repository.findAll()).thenReturn(List.of(cats, dogs));

        List<CategoryDTO> result = service.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Cats");
        assertThat(result.get(1).getName()).isEqualTo("Dogs");
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCategories - empty repo - returns empty list")
    void getAllCategories_emptyRepo_returnsEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDTO> result = service.getAllCategories();

        assertThat(result).isEmpty();
    }

    

    @Test
    @DisplayName("getById - found - returns correct DTO")
    void getById_found_returnsCorrectDTO() {
        when(repository.findById(1)).thenReturn(Optional.of(cats));

        CategoryDTO result = service.getById(1);

        assertThat(result.getCategoryId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Cats");
        verify(repository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById - not found - throws RuntimeException")
    void getById_notFound_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    

    @Test
    @DisplayName("getByName - name exists - returns correct DTO")
    void getByName_exists_returnsCorrectDTO() {
        when(repository.findByName("Dogs")).thenReturn(dogs);

        CategoryDTO result = service.getByName("Dogs");

        assertThat(result.getCategoryId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("Dogs");
    }

    @Test
    @DisplayName("getByName - name not found - throws RuntimeException")
    void getByName_notFound_throwsException() {
        when(repository.findByName("Unknown")).thenReturn(null);

        assertThatThrownBy(() -> service.getByName("Unknown"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    

    @Test
    @DisplayName("add - valid DTO - saves and returns DTO with id")
    void add_validDTO_savesAndReturnsDTO() {
        CategoryDTO inputDTO = new CategoryDTO();
        inputDTO.setName("Rabbits");

        PetCategory savedEntity = new PetCategory(3, "Rabbits");

        when(repository.save(any(PetCategory.class))).thenReturn(savedEntity);

        CategoryDTO result = service.add(inputDTO);

        assertThat(result.getCategoryId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Rabbits");
        verify(repository, times(1)).save(any(PetCategory.class));
    }

   

    @Test
    @DisplayName("update - found - updates name and returns updated DTO")
    void update_found_updatesNameAndReturnsDTO() {
        CategoryDTO updateDTO = new CategoryDTO();
        updateDTO.setName("Big Cats");

        PetCategory updatedEntity = new PetCategory(1, "Big Cats");

        when(repository.findById(1)).thenReturn(Optional.of(cats));
        when(repository.save(any(PetCategory.class))).thenReturn(updatedEntity);

        CategoryDTO result = service.update(1, updateDTO);

        assertThat(result.getCategoryId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Big Cats");
        verify(repository).findById(1);
        verify(repository).save(any(PetCategory.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    

    @Test
    @DisplayName("getById - DTO fields are correctly mapped from entity")
    void getById_allFieldsMappedCorrectly() {
        when(repository.findById(2)).thenReturn(Optional.of(dogs));

        CategoryDTO result = service.getById(2);

        assertThat(result.getCategoryId()).isEqualTo(dogs.getCategoryId());
        assertThat(result.getName()).isEqualTo(dogs.getName());
    }
}

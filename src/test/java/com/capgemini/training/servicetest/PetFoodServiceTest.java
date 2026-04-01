package com.capgemini.training.servicetest;

import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.entity.PetFood;
import com.capgemini.training.repository.PetFoodRepository;
import com.capgemini.training.service.PetFoodService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetFoodServiceTest {

    @Mock
    private PetFoodRepository repo;

    @InjectMocks
    private PetFoodService service;

    private PetFood food1;
    private PetFood food2;

    @BeforeEach
    void setUp() {
        food1 = new PetFood();
        food1.setFoodId(1);
        food1.setName("Pedigree Adult");
        food1.setBrand("Pedigree");
        food1.setType("Dry Food");
        food1.setQuantity(50);
        food1.setPrice(new BigDecimal("1200.00"));

        food2 = new PetFood();
        food2.setFoodId(2);
        food2.setName("Whiskas Pouch");
        food2.setBrand("Whiskas");
        food2.setType("Wet Food");
        food2.setQuantity(10);
        food2.setPrice(new BigDecimal("900.00"));
    }

  

    @Test
    @DisplayName("getAllFiltered - no filters - returns all foods")
    void getAllFiltered_noFilters_returnsAll() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(null, null, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBrand()).isEqualTo("Pedigree");
        assertThat(result.get(1).getBrand()).isEqualTo("Whiskas");
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllFiltered - empty list - returns empty")
    void getAllFiltered_emptyRepo_returnsEmpty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        List<PetFoodDTO> result = service.getAllFiltered(null, null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllFiltered - minPrice filter - filters out cheap food")
    void getAllFiltered_minPriceFilter_returnsOnlyExpensiveFood() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(new BigDecimal("1000.00"), null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pedigree Adult");
    }

    @Test
    @DisplayName("getAllFiltered - maxPrice filter - filters out expensive food")
    void getAllFiltered_maxPriceFilter_returnsCheapFoodOnly() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(null, new BigDecimal("1000.00"), null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Whiskas Pouch");
    }

    @Test
    @DisplayName("getAllFiltered - minQuantity filter - filters out low stock")
    void getAllFiltered_minQuantityFilter_returnsHighStockOnly() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(null, null, 20, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("getAllFiltered - maxQuantity filter - filters out high stock")
    void getAllFiltered_maxQuantityFilter_returnsLowStockOnly() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(null, null, null, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("getAllFiltered - all filters combined - returns only matching")
    void getAllFiltered_allFilters_returnsMatchingOnly() {
        when(repo.findAll()).thenReturn(List.of(food1, food2));

        List<PetFoodDTO> result = service.getAllFiltered(
                new BigDecimal("1000.00"), new BigDecimal("1500.00"), 10, 60);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pedigree Adult");
    }

  

    @Test
    @DisplayName("getByIdDTO - found - returns correct DTO")
    void getByIdDTO_found_returnsDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(food1));

        PetFoodDTO result = service.getByIdDTO(1);

        assertThat(result.getFoodId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Pedigree Adult");
        assertThat(result.getBrand()).isEqualTo("Pedigree");
        assertThat(result.getType()).isEqualTo("Dry Food");
        assertThat(result.getQuantity()).isEqualTo(50);
        assertThat(result.getPrice()).isEqualByComparingTo("1200.00");
        verify(repo, times(1)).findById(1);
    }

    @Test
    @DisplayName("getByIdDTO - not found - throws RuntimeException")
    void getByIdDTO_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdDTO(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Food not found");
    }



    @Test
    @DisplayName("add - valid DTO - saves and returns DTO with id")
    void add_validDTO_savesAndReturnsDTO() {
        PetFoodDTO inputDTO = new PetFoodDTO();
        inputDTO.setName("Royal Canin");
        inputDTO.setBrand("Royal Canin");
        inputDTO.setType("Dry Food");
        inputDTO.setQuantity(20);
        inputDTO.setPrice(new BigDecimal("2500.00"));

        PetFood savedEntity = new PetFood();
        savedEntity.setFoodId(3);
        savedEntity.setName("Royal Canin");
        savedEntity.setBrand("Royal Canin");
        savedEntity.setType("Dry Food");
        savedEntity.setQuantity(20);
        savedEntity.setPrice(new BigDecimal("2500.00"));

        when(repo.save(any(PetFood.class))).thenReturn(savedEntity);

        PetFoodDTO result = service.add(inputDTO);

        assertThat(result.getFoodId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Royal Canin");
        assertThat(result.getBrand()).isEqualTo("Royal Canin");
        assertThat(result.getQuantity()).isEqualTo(20);
        assertThat(result.getPrice()).isEqualByComparingTo("2500.00");
        verify(repo, times(1)).save(any(PetFood.class));
    }



    @Test
    @DisplayName("update - found - updates fields and returns updated DTO")
    void update_found_updatesAndReturnsDTO() {
        PetFoodDTO updateDTO = new PetFoodDTO();
        updateDTO.setName("Pedigree Senior");
        updateDTO.setBrand("Pedigree");
        updateDTO.setType("Dry Food");
        updateDTO.setQuantity(60);
        updateDTO.setPrice(new BigDecimal("1300.00"));

        PetFood updatedEntity = new PetFood();
        updatedEntity.setFoodId(1);
        updatedEntity.setName("Pedigree Senior");
        updatedEntity.setBrand("Pedigree");
        updatedEntity.setType("Dry Food");
        updatedEntity.setQuantity(60);
        updatedEntity.setPrice(new BigDecimal("1300.00"));

        when(repo.findById(1)).thenReturn(Optional.of(food1));
        when(repo.save(any(PetFood.class))).thenReturn(updatedEntity);

        PetFoodDTO result = service.update(1, updateDTO);

        assertThat(result.getName()).isEqualTo("Pedigree Senior");
        assertThat(result.getQuantity()).isEqualTo(60);
        assertThat(result.getPrice()).isEqualByComparingTo("1300.00");
        verify(repo).findById(1);
        verify(repo).save(any(PetFood.class));
    }

    @Test
    @DisplayName("update - not found - throws RuntimeException")
    void update_notFound_throwsException() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        PetFoodDTO dto = new PetFoodDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.update(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Food not found");
    }

   

    @Test
    @DisplayName("getByTypeDTO - type exists - returns matching foods")
    void getByTypeDTO_typeExists_returnsMatchingFoods() {
        when(repo.findByType("Dry Food")).thenReturn(List.of(food1));

        List<PetFoodDTO> result = service.getByTypeDTO("Dry Food");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("Dry Food");
    }

    @Test
    @DisplayName("getByTypeDTO - no match - returns empty list")
    void getByTypeDTO_noMatch_returnsEmpty() {
        when(repo.findByType("Unknown")).thenReturn(Collections.emptyList());

        List<PetFoodDTO> result = service.getByTypeDTO("Unknown");

        assertThat(result).isEmpty();
    }

   

    @Test
    @DisplayName("getByBrandDTO - brand exists - returns matching foods")
    void getByBrandDTO_brandExists_returnsMatchingFoods() {
        when(repo.findByBrand("Whiskas")).thenReturn(List.of(food2));

        List<PetFoodDTO> result = service.getByBrandDTO("Whiskas");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Whiskas");
    }

    @Test
    @DisplayName("getByBrandDTO - no match - returns empty list")
    void getByBrandDTO_noMatch_returnsEmpty() {
        when(repo.findByBrand("Unknown")).thenReturn(Collections.emptyList());

        List<PetFoodDTO> result = service.getByBrandDTO("Unknown");

        assertThat(result).isEmpty();
    }

   

    @Test
    @DisplayName("searchByNameDTO - partial name match - returns results")
    void searchByNameDTO_partialMatch_returnsResults() {
        when(repo.findByNameContainingIgnoreCase("pedigree")).thenReturn(List.of(food1));

        List<PetFoodDTO> result = service.searchByNameDTO("pedigree");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pedigree Adult");
    }

    @Test
    @DisplayName("searchByNameDTO - no match - returns empty list")
    void searchByNameDTO_noMatch_returnsEmpty() {
        when(repo.findByNameContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

        List<PetFoodDTO> result = service.searchByNameDTO("xyz");

        assertThat(result).isEmpty();
    }

 

    @Test
    @DisplayName("getByIdDTO - all DTO fields are correctly mapped from entity")
    void getByIdDTO_allFieldsMappedCorrectly() {
        when(repo.findById(1)).thenReturn(Optional.of(food1));

        PetFoodDTO result = service.getByIdDTO(1);

        assertThat(result.getFoodId()).isEqualTo(food1.getFoodId());
        assertThat(result.getName()).isEqualTo(food1.getName());
        assertThat(result.getBrand()).isEqualTo(food1.getBrand());
        assertThat(result.getType()).isEqualTo(food1.getType());
        assertThat(result.getQuantity()).isEqualTo(food1.getQuantity());
        assertThat(result.getPrice()).isEqualByComparingTo(food1.getPrice());
    }
}

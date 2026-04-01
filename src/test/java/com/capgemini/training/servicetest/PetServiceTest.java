package com.capgemini.training.servicetest;

import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.PetFoodDTO;
import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.entity.*;
import com.capgemini.training.repository.*;
import com.capgemini.training.service.PetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetCategoryRepository categoryRepository;

    @Mock
    private PetFoodRepository foodRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PetService service;

    private PetCategory catCategory;
    private PetCategory dogCategory;
    private Pet pet1;
    private Pet pet2;
    private PetFood food1;
    private Supplier supplier1;
    private Address address;
    private Customer customer;
    private Transaction txSuccess;
    private Transaction txFailed;

    @BeforeEach
    void setUp() {
        catCategory = new PetCategory(1, "Cats");
        dogCategory = new PetCategory(2, "Dogs");

        pet1 = new Pet();
        pet1.setPetId(1);
        pet1.setName("Bella");
        pet1.setBreed("Labrador Retriever");
        pet1.setAge(2);
        pet1.setPrice(new BigDecimal("25000.00"));
        pet1.setDescription("Friendly and energetic Labrador");
        pet1.setImageUrl("https://example.com/bella.jpg");
        pet1.setCategory(dogCategory);
        pet1.setFoods(new ArrayList<>());
        pet1.setSuppliers(new ArrayList<>());
        pet1.setVaccinations(new ArrayList<>());
        pet1.setGroomingServices(new ArrayList<>());
        pet1.setEmployees(new ArrayList<>());

        pet2 = new Pet();
        pet2.setPetId(2);
        pet2.setName("Whiskers");
        pet2.setBreed("Persian Cat");
        pet2.setAge(3);
        pet2.setPrice(new BigDecimal("15000.00"));
        pet2.setDescription("Fluffy and calm Persian Cat");
        pet2.setImageUrl("https://example.com/whiskers.jpg");
        pet2.setCategory(catCategory);
        pet2.setFoods(new ArrayList<>());
        pet2.setSuppliers(new ArrayList<>());
        pet2.setVaccinations(new ArrayList<>());
        pet2.setGroomingServices(new ArrayList<>());
        pet2.setEmployees(new ArrayList<>());

        food1 = new PetFood();
        food1.setFoodId(1);
        food1.setName("Pedigree Adult");
        food1.setBrand("Pedigree");
        food1.setType("Dry Food");
        food1.setQuantity(50);
        food1.setPrice(new BigDecimal("1200.00"));

        address = new Address();
        address.setAddressId(1);
        address.setCity("Kolkata");
        address.setState("West Bengal");

        supplier1 = new Supplier();
        supplier1.setSupplierId(1);
        supplier1.setName("Pet World Supplier");
        supplier1.setContactPerson("Ramesh Gupta");
        supplier1.setPhoneNumber("9988776655");
        supplier1.setEmail("supplier@example.com");
        supplier1.setAddress(address);

        customer = new Customer();
        customer.setCustomerId(1);
        customer.setFirstName("Alice");
        customer.setLastName("Johnson");

        txSuccess = new Transaction();
        txSuccess.setTransactionId(1);
        txSuccess.setAmount(new BigDecimal("25000.00"));
        txSuccess.setTransactionDate(LocalDate.of(2024, 1, 15));
        txSuccess.setTransactionStatus(TransactionStatus.Success);
        txSuccess.setCustomer(customer);
        txSuccess.setPet(pet1);

        txFailed = new Transaction();
        txFailed.setTransactionId(2);
        txFailed.setAmount(new BigDecimal("10000.00"));
        txFailed.setTransactionDate(LocalDate.of(2024, 3, 20));
        txFailed.setTransactionStatus(TransactionStatus.Failed);
        txFailed.setCustomer(customer);
        txFailed.setPet(pet1);
    }


    @Test
    @DisplayName("getPetsFiltered - no filters - returns all pets")
    void getPetsFiltered_noFilters_returnsAll() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(null, null, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Bella");
        assertThat(result.get(1).getName()).isEqualTo("Whiskers");
        verify(petRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPetsFiltered - empty repo - returns empty list")
    void getPetsFiltered_emptyRepo_returnsEmpty() {
        when(petRepository.findAll()).thenReturn(Collections.emptyList());

        List<PetDTO> result = service.getPetsFiltered(null, null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPetsFiltered - minPrice filter - returns only expensive pets")
    void getPetsFiltered_minPrice_returnsExpensiveOnly() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(new BigDecimal("20000.00"), null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bella");
    }

    @Test
    @DisplayName("getPetsFiltered - maxPrice filter - returns only cheap pets")
    void getPetsFiltered_maxPrice_returnsCheapOnly() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(null, new BigDecimal("20000.00"), null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Whiskers");
    }

    @Test
    @DisplayName("getPetsFiltered - minAge filter - returns only older pets")
    void getPetsFiltered_minAge_returnsOlderPetsOnly() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(null, null, 3, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Whiskers");
    }

    @Test
    @DisplayName("getPetsFiltered - maxAge filter - returns only younger pets")
    void getPetsFiltered_maxAge_returnsYoungerPetsOnly() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(null, null, null, 2);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bella");
    }

    @Test
    @DisplayName("getPetsFiltered - all filters combined - returns only matching pets")
    void getPetsFiltered_allFilters_returnsOnlyMatching() {
        when(petRepository.findAll()).thenReturn(List.of(pet1, pet2));

        List<PetDTO> result = service.getPetsFiltered(
                new BigDecimal("20000.00"), new BigDecimal("30000.00"), 1, 3);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bella");
    }


    @Test
    @DisplayName("addPet - valid DTO with categoryId - saves and returns DTO")
    void addPet_validDTOWithCategoryId_savesAndReturns() {
        PetDTO inputDTO = new PetDTO();
        inputDTO.setName("Max");
        inputDTO.setBreed("German Shepherd");
        inputDTO.setAge(3);
        inputDTO.setPrice(new BigDecimal("30000.00"));
        inputDTO.setDescription("Intelligent and loyal German Shepherd");
        inputDTO.setImageUrl("https://example.com/max.jpg");
        inputDTO.setCategoryId(2);

        Pet savedEntity = new Pet();
        savedEntity.setPetId(3);
        savedEntity.setName("Max");
        savedEntity.setBreed("German Shepherd");
        savedEntity.setAge(3);
        savedEntity.setPrice(new BigDecimal("30000.00"));
        savedEntity.setDescription("Intelligent and loyal German Shepherd");
        savedEntity.setImageUrl("https://example.com/max.jpg");
        savedEntity.setCategory(dogCategory);

        when(categoryRepository.findById(2)).thenReturn(Optional.of(dogCategory));
        when(petRepository.save(any(Pet.class))).thenReturn(savedEntity);

        PetDTO result = service.addPet(inputDTO);

        assertThat(result.getPetId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Max");
        assertThat(result.getBreed()).isEqualTo("German Shepherd");
        assertThat(result.getCategoryId()).isEqualTo(2);
        assertThat(result.getCategoryName()).isEqualTo("Dogs");
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("addPet - valid DTO with categoryName - saves using name lookup")
    void addPet_validDTOWithCategoryName_savesUsingNameLookup() {
        PetDTO inputDTO = new PetDTO();
        inputDTO.setName("Luna");
        inputDTO.setBreed("Golden Retriever");
        inputDTO.setAge(1);
        inputDTO.setPrice(new BigDecimal("20000.00"));
        inputDTO.setCategoryName("Dogs");

        Pet savedEntity = new Pet();
        savedEntity.setPetId(4);
        savedEntity.setName("Luna");
        savedEntity.setBreed("Golden Retriever");
        savedEntity.setAge(1);
        savedEntity.setPrice(new BigDecimal("20000.00"));
        savedEntity.setCategory(dogCategory);

        when(categoryRepository.findByName("Dogs")).thenReturn(dogCategory);
        when(petRepository.save(any(Pet.class))).thenReturn(savedEntity);

        PetDTO result = service.addPet(inputDTO);

        assertThat(result.getPetId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("Luna");
        assertThat(result.getCategoryName()).isEqualTo("Dogs");
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("addPet - valid DTO without category - saves with null category")
    void addPet_noCategoryProvided_savesWithNullCategory() {
        PetDTO inputDTO = new PetDTO();
        inputDTO.setName("Unknown Pet");
        inputDTO.setBreed("Unknown");
        inputDTO.setAge(1);
        inputDTO.setPrice(new BigDecimal("5000.00"));

        Pet savedEntity = new Pet();
        savedEntity.setPetId(5);
        savedEntity.setName("Unknown Pet");
        savedEntity.setCategory(null);

        when(petRepository.save(any(Pet.class))).thenReturn(savedEntity);

        PetDTO result = service.addPet(inputDTO);

        assertThat(result.getPetId()).isEqualTo(5);
        assertThat(result.getCategoryId()).isNull();
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("addPet - categoryId not found - throws RuntimeException")
    void addPet_categoryIdNotFound_throwsException() {
        PetDTO inputDTO = new PetDTO();
        inputDTO.setName("Max");
        inputDTO.setCategoryId(99);

        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addPet(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    @Test
    @DisplayName("addPet - categoryName not found - throws RuntimeException")
    void addPet_categoryNameNotFound_throwsException() {
        PetDTO inputDTO = new PetDTO();
        inputDTO.setName("Max");
        inputDTO.setCategoryName("UnknownAnimal");

        when(categoryRepository.findByName("UnknownAnimal")).thenReturn(null);

        assertThatThrownBy(() -> service.addPet(inputDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }


    @Test
    @DisplayName("updatePet - found - updates all fields and returns updated DTO")
    void updatePet_found_updatesAndReturns() {
        PetDTO updateDTO = new PetDTO();
        updateDTO.setName("Bella Updated");
        updateDTO.setBreed("Labrador Retriever");
        updateDTO.setAge(3);
        updateDTO.setPrice(new BigDecimal("28000.00"));
        updateDTO.setDescription("Updated description");
        updateDTO.setImageUrl("https://example.com/bella_new.jpg");
        updateDTO.setCategoryId(2);

        Pet updatedEntity = new Pet();
        updatedEntity.setPetId(1);
        updatedEntity.setName("Bella Updated");
        updatedEntity.setBreed("Labrador Retriever");
        updatedEntity.setAge(3);
        updatedEntity.setPrice(new BigDecimal("28000.00"));
        updatedEntity.setDescription("Updated description");
        updatedEntity.setImageUrl("https://example.com/bella_new.jpg");
        updatedEntity.setCategory(dogCategory);

        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(dogCategory));
        when(petRepository.save(any(Pet.class))).thenReturn(updatedEntity);

        PetDTO result = service.updatePet(1, updateDTO);

        assertThat(result.getName()).isEqualTo("Bella Updated");
        assertThat(result.getAge()).isEqualTo(3);
        assertThat(result.getPrice()).isEqualByComparingTo("28000.00");
        verify(petRepository).findById(1);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    @DisplayName("updatePet - not found - throws RuntimeException")
    void updatePet_notFound_throwsException() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        PetDTO dto = new PetDTO();
        dto.setName("Test");

        assertThatThrownBy(() -> service.updatePet(99, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }


    @Test
    @DisplayName("getPetFoods - pet exists with foods - returns food DTOs")
    void getPetFoods_petWithFoods_returnsFoodDTOs() {
        pet1.getFoods().add(food1);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));

        List<PetFoodDTO> result = service.getPetFoods(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pedigree Adult");
        assertThat(result.get(0).getBrand()).isEqualTo("Pedigree");
        assertThat(result.get(0).getType()).isEqualTo("Dry Food");
        assertThat(result.get(0).getQuantity()).isEqualTo(50);
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("1200.00");
    }

    @Test
    @DisplayName("getPetFoods - pet exists with no foods - returns empty list")
    void getPetFoods_petWithNoFoods_returnsEmpty() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));

        List<PetFoodDTO> result = service.getPetFoods(1);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPetFoods - pet not found - throws RuntimeException")
    void getPetFoods_petNotFound_throwsException() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPetFoods(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }


    @Test
    @DisplayName("getSuppliersDTO - pet exists with suppliers - returns supplier DTOs")
    void getSuppliersDTO_petWithSuppliers_returnsSupplierDTOs() {
        pet1.getSuppliers().add(supplier1);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));

        List<SupplierDTO> result = service.getSuppliersDTO(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pet World Supplier");
        assertThat(result.get(0).getCity()).isEqualTo("Kolkata");
        assertThat(result.get(0).getState()).isEqualTo("West Bengal");
        assertThat(result.get(0).getSupplierId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getSuppliersDTO - pet exists with no suppliers - returns empty list")
    void getSuppliersDTO_petWithNoSuppliers_returnsEmpty() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));

        List<SupplierDTO> result = service.getSuppliersDTO(1);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getSuppliersDTO - supplier without address - city and state are null")
    void getSuppliersDTO_supplierWithoutAddress_cityAndStateNull() {
        Supplier supplierNoAddr = new Supplier();
        supplierNoAddr.setSupplierId(2);
        supplierNoAddr.setName("No Address Supplier");
        supplierNoAddr.setAddress(null);

        pet1.getSuppliers().add(supplierNoAddr);
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));

        List<SupplierDTO> result = service.getSuppliersDTO(1);

        assertThat(result.get(0).getCity()).isNull();
        assertThat(result.get(0).getState()).isNull();
    }

    @Test
    @DisplayName("getSuppliersDTO - pet not found - throws RuntimeException")
    void getSuppliersDTO_petNotFound_throwsException() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSuppliersDTO(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }


    @Test
    @DisplayName("getTransactionHistory - pet has transactions - returns transaction DTOs")
    void getTransactionHistory_petHasTransactions_returnsTransactionDTOs() {
        when(transactionRepository.findAll()).thenReturn(List.of(txSuccess, txFailed));

        List<TransactionDTO> result = service.getTransactionHistory(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("Success");
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("25000.00");
        assertThat(result.get(0).getTransactionDate()).isEqualTo("2024-01-15");
        assertThat(result.get(1).getStatus()).isEqualTo("Failed");
    }

    @Test
    @DisplayName("getTransactionHistory - pet has no transactions - returns empty list")
    void getTransactionHistory_petHasNoTransactions_returnsEmpty() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<TransactionDTO> result = service.getTransactionHistory(1);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getTransactionHistory - filters transactions by petId correctly")
    void getTransactionHistory_filtersCorrectlyByPetId() {
        Pet anotherPet = new Pet();
        anotherPet.setPetId(99);

        Transaction txOther = new Transaction();
        txOther.setTransactionId(3);
        txOther.setAmount(new BigDecimal("5000.00"));
        txOther.setTransactionDate(LocalDate.of(2024, 5, 1));
        txOther.setTransactionStatus(TransactionStatus.Success);
        txOther.setCustomer(customer);
        txOther.setPet(anotherPet);

        when(transactionRepository.findAll()).thenReturn(List.of(txSuccess, txOther));

        List<TransactionDTO> result = service.getTransactionHistory(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo(1);
    }


    @Test
    @DisplayName("assignFood - pet and food exist - assigns food and returns updated pet DTO")
    void assignFood_bothExist_assignsAndReturnsPetDTO() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(foodRepository.findById(1)).thenReturn(Optional.of(food1));
        when(petRepository.save(any(Pet.class))).thenReturn(pet1);

        PetDTO result = service.assignFood(1, 1);

        assertThat(pet1.getFoods()).hasSize(1);
        assertThat(pet1.getFoods().get(0).getName()).isEqualTo("Pedigree Adult");
        verify(petRepository).save(pet1);
    }

    @Test
    @DisplayName("assignFood - pet not found - throws RuntimeException")
    void assignFood_petNotFound_throwsException() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignFood(99, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }

    @Test
    @DisplayName("assignFood - food not found - throws RuntimeException")
    void assignFood_foodNotFound_throwsException() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(foodRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignFood(1, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Food not found");
    }


    @Test
    @DisplayName("assignSupplier - pet and supplier exist - assigns and returns pet DTO")
    void assignSupplier_bothExist_assignsAndReturnsPetDTO() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier1));
        when(petRepository.save(any(Pet.class))).thenReturn(pet1);

        PetDTO result = service.assignSupplier(1, 1);

        assertThat(pet1.getSuppliers()).hasSize(1);
        assertThat(pet1.getSuppliers().get(0).getName()).isEqualTo("Pet World Supplier");
        verify(petRepository).save(pet1);
    }

    @Test
    @DisplayName("assignSupplier - already assigned - does not add duplicate")
    void assignSupplier_alreadyAssigned_doesNotAddDuplicate() {
        pet1.getSuppliers().add(supplier1);

        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier1));
        when(petRepository.save(any(Pet.class))).thenReturn(pet1);

        service.assignSupplier(1, 1);

        assertThat(pet1.getSuppliers()).hasSize(1);
        verify(petRepository).save(pet1);
    }

    @Test
    @DisplayName("assignSupplier - pet not found - throws RuntimeException")
    void assignSupplier_petNotFound_throwsException() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignSupplier(99, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pet not found");
    }

    @Test
    @DisplayName("assignSupplier - supplier not found - throws RuntimeException")
    void assignSupplier_supplierNotFound_throwsException() {
        when(petRepository.findById(1)).thenReturn(Optional.of(pet1));
        when(supplierRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignSupplier(1, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Supplier not found");
    }


    @Test
    @DisplayName("getPetsFiltered - DTO all fields are correctly mapped from entity")
    void getPetsFiltered_allFieldsMappedCorrectly() {
        when(petRepository.findAll()).thenReturn(List.of(pet1));

        List<PetDTO> result = service.getPetsFiltered(null, null, null, null);

        PetDTO dto = result.get(0);
        assertThat(dto.getPetId()).isEqualTo(pet1.getPetId());
        assertThat(dto.getName()).isEqualTo(pet1.getName());
        assertThat(dto.getBreed()).isEqualTo(pet1.getBreed());
        assertThat(dto.getAge()).isEqualTo(pet1.getAge());
        assertThat(dto.getPrice()).isEqualByComparingTo(pet1.getPrice());
        assertThat(dto.getDescription()).isEqualTo(pet1.getDescription());
        assertThat(dto.getImageUrl()).isEqualTo(pet1.getImageUrl());
        assertThat(dto.getCategoryId()).isEqualTo(dogCategory.getCategoryId());
        assertThat(dto.getCategoryName()).isEqualTo(dogCategory.getName());
    }

    @Test
    @DisplayName("getPetsFiltered - pet without category - categoryId and categoryName are null")
    void getPetsFiltered_petWithoutCategory_categoryFieldsNull() {
        pet1.setCategory(null);
        when(petRepository.findAll()).thenReturn(List.of(pet1));

        List<PetDTO> result = service.getPetsFiltered(null, null, null, null);

        assertThat(result.get(0).getCategoryId()).isNull();
        assertThat(result.get(0).getCategoryName()).isNull();
    }
}
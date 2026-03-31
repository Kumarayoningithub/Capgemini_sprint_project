package com.capgemini.training;

import com.capgemini.training.controller.SupplierController;
import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.SupplierService;
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

@WebMvcTest(controllers = SupplierController.class)
@Import(GlobalExceptionHandler.class)
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SupplierService supplierService;

    private SupplierDTO buildSupplier(int id, String name, String contact, String phone, String email,
                                      String city, String state, String street, String zipcode) {
        SupplierDTO dto = new SupplierDTO();
        dto.setSupplierId(id);
        dto.setName(name);
        dto.setContactPerson(contact);
        dto.setPhoneNumber(phone);
        dto.setEmail(email);
        dto.setCity(city);
        dto.setState(state);
        dto.setStreet(street);
        dto.setZipcode(zipcode);
        return dto;
    }

    private SupplierDTO buildSupplierAddBody() {
        SupplierDTO dto = new SupplierDTO();
        dto.setName("Mumbai Wholesale");
        dto.setContactPerson("Priya Nair");
        dto.setPhoneNumber("9000000000");
        dto.setEmail("mumbai@example.com");

        SupplierDTO.AddressRef addr = new SupplierDTO.AddressRef();
        addr.setAddressId(1);
        dto.setAddress(addr);

        return dto;
    }

    @Test
    @DisplayName("GET /suppliers returns 200 with Mumbai and Kolkata suppliers")
    void getAll_returns200() throws Exception {
        when(supplierService.getAllDTO()).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016"),
                buildSupplier(2, "Delhi Pet Mart", "Neha Verma", "9123456789",
                        "delhi@example.com", "Delhi", "Delhi",
                        "Connaught Place", "110001")));

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].city").value("Kolkata"));
    }

    @Test
    @DisplayName("GET /suppliers empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(supplierService.getAllDTO()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /suppliers/name/{name} returns 200")
    void getByName_returns200() throws Exception {
        when(supplierService.getByNameDTO("Pet World Supplier")).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016")));

        mockMvc.perform(get("/suppliers/name/{name}", "Pet World Supplier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pet World Supplier"));
    }

    @Test
    @DisplayName("GET /suppliers/city/{city} returns 200")
    void getByCity_returns200() throws Exception {
        when(supplierService.getByCityDTO("Kolkata")).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016")));

        mockMvc.perform(get("/suppliers/city/{city}", "Kolkata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("West Bengal"));
    }

    @Test
    @DisplayName("GET /suppliers/state/{state} returns 200")
    void getByState_returns200() throws Exception {
        when(supplierService.getByStateDTO("West Bengal")).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016")));

        mockMvc.perform(get("/suppliers/state/{state}", "West Bengal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Kolkata"));
    }

    @Test
    @DisplayName("GET /suppliers/street/{street} returns 200")
    void getByStreet_returns200() throws Exception {
        when(supplierService.getByStreetDTO("Park Street")).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016")));

        mockMvc.perform(get("/suppliers/street/{street}", "Park Street"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].street").value("Park Street"));
    }

    @Test
    @DisplayName("GET /suppliers/zipcode/{zipcode} returns 200")
    void getByZipcode_returns200() throws Exception {
        when(supplierService.getByZipcodeDTO("700016")).thenReturn(List.of(
                buildSupplier(1, "Pet World Supplier", "Ramesh Gupta", "9988776655",
                        "supplier@example.com", "Kolkata", "West Bengal",
                        "Park Street", "700016")));

        mockMvc.perform(get("/suppliers/zipcode/{zipcode}", "700016"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].zipcode").value("700016"));
    }

    @Test
    @DisplayName("GET /suppliers/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(supplierService.getByIdDTO(1)).thenReturn(
                buildSupplier(1, "Royal Pet Supplier", "Suresh Kumar", "9871234560",
                        "royalpet@example.com", "Kolkata", "West Bengal",
                        "Salt Lake", "700091"));

        mockMvc.perform(get("/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplierId").value(1))
                .andExpect(jsonPath("$.contactPerson").value("Suresh Kumar"));
    }

    @Test
    @DisplayName("GET /suppliers/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(supplierService.getByIdDTO(99)).thenThrow(new RuntimeException("Supplier not found"));

        mockMvc.perform(get("/suppliers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /suppliers/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(supplierService.add(any(SupplierDTO.class))).thenReturn(
                buildSupplier(2, "Mumbai Wholesale", "Priya Nair", "9000000000",
                        "mumbai@example.com", "Mumbai", "Maharashtra",
                        "Marine Drive", "400001"));

        mockMvc.perform(post("/suppliers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildSupplierAddBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mumbai Wholesale"));
    }

    @Test
    @DisplayName("PUT /suppliers/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(supplierService.update(eq(1), any(SupplierDTO.class))).thenReturn(
                buildSupplier(1, "Royal Pet Supplier", "Suresh Kumar", "9871234560",
                        "royalpet@example.com", "Kolkata", "West Bengal",
                        "Salt Lake", "700091"));

        mockMvc.perform(put("/suppliers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Royal Pet Supplier","contactPerson":"Suresh Kumar","phoneNumber":"9871234560","email":"royalpet@example.com","address":{"addressId":1}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("royalpet@example.com"));
    }

    @Test
    @DisplayName("PUT /suppliers/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(supplierService.update(eq(99), any(SupplierDTO.class)))
                .thenThrow(new RuntimeException("Supplier not found for update"));

        mockMvc.perform(put("/suppliers/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"contactPerson\":\"Y\",\"phoneNumber\":\"1\",\"email\":\"e@e.com\",\"address\":{\"addressId\":1}}"))
                .andExpect(status().isNotFound());
    }
}

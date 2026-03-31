package com.capgemini.training;

import com.capgemini.training.controller.AddressController;
import com.capgemini.training.entity.Address;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.AddressService;
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

@WebMvcTest(controllers = AddressController.class)
@Import(GlobalExceptionHandler.class)
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    private Address buildAddress(int id, String street, String city, String state, String zip) {
        Address a = new Address();
        a.setAddressId(id);
        a.setStreet(street);
        a.setCity(city);
        a.setState(state);
        a.setZipCode(zip);
        return a;
    }

    @Test
    @DisplayName("GET /address returns 200 with list size and Kolkata addresses")
    void getAll_returns200WithFields() throws Exception {
        List<Address> list = List.of(
                buildAddress(1, "12 Park Street", "Kolkata", "West Bengal", "700016"),
                buildAddress(2, "Marine Drive", "Mumbai", "Maharashtra", "400020"));
        when(addressService.getAll()).thenReturn(list);

        mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].addressId").value(1))
                .andExpect(jsonPath("$[0].city").value("Kolkata"))
                .andExpect(jsonPath("$[1].city").value("Mumbai"));
    }

    @Test
    @DisplayName("GET /address with empty list returns 200 and empty array")
    void getAll_empty_returns200() throws Exception {
        when(addressService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /address/{id} when found returns 200 and fields")
    void getById_found_returns200() throws Exception {
        when(addressService.getById(1)).thenReturn(
                buildAddress(1, "Salt Lake Sector V", "Kolkata", "West Bengal", "700091"));

        mockMvc.perform(get("/address/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(1))
                .andExpect(jsonPath("$.street").value("Salt Lake Sector V"))
                .andExpect(jsonPath("$.zipCode").value("700091"));
    }

    @Test
    @DisplayName("GET /address/{id} when not found returns 404 via GlobalExceptionHandler")
    void getById_notFound_returns404() throws Exception {
        when(addressService.getById(99)).thenThrow(new RuntimeException("Address not found"));

        mockMvc.perform(get("/address/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /address/add with valid body returns 200 and saved fields")
    void add_valid_returns200() throws Exception {
        Address saved = buildAddress(3, "Connaught Place", "Delhi", "Delhi", "110001");
        when(addressService.add(any(Address.class))).thenReturn(saved);

        Address input = buildAddress(0, "Connaught Place", "Delhi", "Delhi", "110001");
        mockMvc.perform(post("/address/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressId").value(3))
                .andExpect(jsonPath("$.city").value("Delhi"));
    }

    @Test
    @DisplayName("PUT /address/update/{id} when found returns 200 and updated fields")
    void update_found_returns200() throws Exception {
        Address updated = buildAddress(1, "Updated Street", "Kolkata", "West Bengal", "700001");
        when(addressService.update(eq(1), any(Address.class))).thenReturn(updated);

        mockMvc.perform(put("/address/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"street\":\"Updated Street\",\"city\":\"Kolkata\",\"state\":\"West Bengal\",\"zipCode\":\"700001\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Updated Street"));
    }

    @Test
    @DisplayName("PUT /address/update/{id} when not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(addressService.update(eq(99), any(Address.class)))
                .thenThrow(new RuntimeException("Address not found for update"));

        mockMvc.perform(put("/address/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"street\":\"X\",\"city\":\"Y\",\"state\":\"Z\",\"zipCode\":\"000\"}"))
                .andExpect(status().isNotFound());
    }
}

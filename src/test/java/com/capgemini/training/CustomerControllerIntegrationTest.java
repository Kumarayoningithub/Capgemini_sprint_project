package com.capgemini.training;

import com.capgemini.training.controller.CustomerController;
import com.capgemini.training.dto.CustomerDTO;
import com.capgemini.training.dto.PetDTO;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.CustomerService;
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

@WebMvcTest(controllers = CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerDTO buildCustomer(int id, String first, String last, String email, String phone,
                                      String city, String state) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(id);
        dto.setFirstName(first);
        dto.setLastName(last);
        dto.setEmail(email);
        dto.setPhoneNumber(phone);
        dto.setCity(city);
        dto.setState(state);
        return dto;
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

    private PetDTO buildPet(int id, String name) {
        PetDTO p = new PetDTO();
        p.setPetId(id);
        p.setName(name);
        p.setBreed("Golden Retriever");
        p.setAge(2);
        p.setPrice(new BigDecimal("45000.00"));
        p.setCategoryName("Dogs");
        return p;
    }

    private CustomerDTO buildCustomerAddBody() {
        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Rahul");
        dto.setLastName("Sharma");
        dto.setEmail("rahul@example.com");
        dto.setPhoneNumber("9123456780");
        CustomerDTO.AddressRef addr = new CustomerDTO.AddressRef();
        addr.setAddressId(1);
        dto.setAddress(addr);
        return dto;
    }

    @Test
    @DisplayName("GET /customers returns 200 with Ayon and Raj")
    void getAll_returns200() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of(
                buildCustomer(1, "Ayon", "Saha", "ayon@example.com", "9876543210", "Kolkata", "West Bengal"),
                buildCustomer(2, "Raj", "Patel", "raj@example.com", "9123456780", "Mumbai", "Maharashtra")));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Ayon"))
                .andExpect(jsonPath("$[1].city").value("Mumbai"));
    }

    @Test
    @DisplayName("GET /customers empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /customers/no-transactions returns 200")
    void getNoTransactions_returns200() throws Exception {
        when(customerService.getCustomersWithoutTransactions()).thenReturn(List.of(
                buildCustomer(3, "Priya", "Iyer", "priya@example.com", "9000000001", "Chennai", "Tamil Nadu")));

        mockMvc.perform(get("/customers/no-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Iyer"));
    }

    @Test
    @DisplayName("GET /customers/name/{first}/{last} returns 200")
    void getByName_returns200() throws Exception {
        when(customerService.getByName("Ayon", "Saha")).thenReturn(List.of(
                buildCustomer(1, "Ayon", "Saha", "ayon@example.com", "9876543210", "Kolkata", "West Bengal")));

        mockMvc.perform(get("/customers/name/{first}/{last}", "Ayon", "Saha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("ayon@example.com"));
    }

    @Test
    @DisplayName("GET /customers/by_city/{city} returns 200")
    void getByCity_returns200() throws Exception {
        when(customerService.getByCity("Kolkata")).thenReturn(List.of(
                buildCustomer(1, "Ayon", "Saha", "ayon@example.com", "9876543210", "Kolkata", "West Bengal")));

        mockMvc.perform(get("/customers/by_city/{city}", "Kolkata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("West Bengal"));
    }

    @Test
    @DisplayName("GET /customers/by_state/{state} returns 200")
    void getByState_returns200() throws Exception {
        when(customerService.getByState("West Bengal")).thenReturn(List.of(
                buildCustomer(1, "Ayon", "Saha", "ayon@example.com", "9876543210", "Kolkata", "West Bengal")));

        mockMvc.perform(get("/customers/by_state/{state}", "West Bengal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("Kolkata"));
    }

    @Test
    @DisplayName("GET /customers/transactions/{customerId} returns 200")
    void getTransactions_returns200() throws Exception {
        when(customerService.getTransactions(1)).thenReturn(List.of(buildTransaction(1, "Success")));

        mockMvc.perform(get("/customers/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Success"));
    }

    @Test
    @DisplayName("GET /customers/transactions_status/{status} returns 200")
    void getTransactionsByStatus_returns200() throws Exception {
        when(customerService.getTransactionsByStatus("Success")).thenReturn(List.of(buildTransaction(1, "Success")));

        mockMvc.perform(get("/customers/transactions_status/{status}", "Success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(1));
    }

    @Test
    @DisplayName("GET /customers/pets/{customerId} returns 200")
    void getPets_returns200() throws Exception {
        when(customerService.getCustomerPets(1)).thenReturn(List.of(buildPet(1, "Tommy")));

        mockMvc.perform(get("/customers/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tommy"));
    }

    @Test
    @DisplayName("GET /customers/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(customerService.getById(1)).thenReturn(
                buildCustomer(1, "Ayon", "Saha", "ayon@example.com", "9876543210", "Kolkata", "West Bengal"));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"));
    }

    @Test
    @DisplayName("GET /customers/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(customerService.getById(99)).thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /customers/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(customerService.add(any(CustomerDTO.class))).thenReturn(
                buildCustomer(5, "Rahul", "Sharma", "rahul@example.com", "9123456780", "Delhi", "Delhi"));

        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCustomerAddBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Rahul"));
    }

    @Test
    @DisplayName("PUT /customers/update/{id} found returns 200")
    void update_found_returns200() throws Exception {
        when(customerService.update(eq(1), any(CustomerDTO.class))).thenReturn(
                buildCustomer(1, "Rahul", "Sharma", "rahul@example.com", "9123456780", "Kolkata", "West Bengal"));

        mockMvc.perform(put("/customers/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Rahul","lastName":"Sharma","email":"rahul@example.com","phoneNumber":"9123456780","address":{"addressId":1}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Sharma"));
    }

    @Test
    @DisplayName("PUT /customers/update/{id} not found returns 404")
    void update_notFound_returns404() throws Exception {
        when(customerService.update(eq(99), any(CustomerDTO.class)))
                .thenThrow(new RuntimeException("Customer not found for update"));

        mockMvc.perform(put("/customers/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"X\",\"lastName\":\"Y\",\"email\":\"z@z.com\",\"phoneNumber\":\"1\",\"address\":{\"addressId\":1}}"))
                .andExpect(status().isNotFound());
    }
}

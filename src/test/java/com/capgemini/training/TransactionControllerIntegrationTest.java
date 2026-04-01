package com.capgemini.training;

import com.capgemini.training.controller.TransactionController;
import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.exception.GlobalExceptionHandler;
import com.capgemini.training.service.TransactionService;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionDTO buildTransaction(int tid, int custId, String custName, int petId, String petName,
                                            BigDecimal amount, String status, String date) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tid);
        dto.setCustomerId(custId);
        dto.setCustomerName(custName);
        dto.setPetId(petId);
        dto.setPetName(petName);
        dto.setAmount(amount);
        dto.setStatus(status);
        dto.setTransactionDate(date);
        return dto;
    }

    private TransactionDTO buildTransactionPostBody() {
        TransactionDTO dto = new TransactionDTO();
        dto.setCustomerId(1);
        dto.setPetId(1);
        dto.setTransactionDate("2026-03-28");
        dto.setAmount(new BigDecimal("45000.00"));
        dto.setStatus("Success");
        return dto;
    }

    @Test
    @DisplayName("GET /transaction_history returns 200")
    void getAll_returns200() throws Exception {
        when(transactionService.getAll(isNull(), isNull())).thenReturn(List.of(
                buildTransaction(1, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28")));

        mockMvc.perform(get("/transaction_history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("Success"));
    }

    @Test
    @DisplayName("GET /transaction_history empty returns 200")
    void getAll_empty_returns200() throws Exception {
        when(transactionService.getAll(isNull(), isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transaction_history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /transaction_history with minDate and maxDate returns filtered list")
    void getAll_withDateFilters_returns200() throws Exception {
        when(transactionService.getAll(eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 12, 31))))
                .thenReturn(List.of(buildTransaction(1, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28")));

        mockMvc.perform(get("/transaction_history")
                        .param("minDate", "2026-01-01")
                        .param("maxDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

 

    @Test
    @DisplayName("GET /transaction_history/by_customer/{customerId} returns 200")
    void getByCustomer_returns200() throws Exception {
        when(transactionService.getByCustomer(1)).thenReturn(List.of(
                buildTransaction(1, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28")));

        mockMvc.perform(get("/transaction_history/by_customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Ayon Saha"));
    }

    @Test
    @DisplayName("GET /transaction_history/success returns 200")
    void getSuccess_returns200() throws Exception {
        when(transactionService.getSuccessTransactions()).thenReturn(List.of(
                buildTransaction(1, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28")));

        mockMvc.perform(get("/transaction_history/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Success"));
    }

    @Test
    @DisplayName("GET /transaction_history/failed returns 200")
    void getFailed_returns200() throws Exception {
        when(transactionService.getFailedTransactions()).thenReturn(List.of(
                buildTransaction(2, 2, "Raj Mehta", 2, "Milo", new BigDecimal("1000.00"), "Failed", "2026-02-01")));

        mockMvc.perform(get("/transaction_history/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Failed"));
    }

 

    @Test
    @DisplayName("GET /transaction_history/{id} found returns 200")
    void getById_found_returns200() throws Exception {
        when(transactionService.getById(1)).thenReturn(
                buildTransaction(1, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28"));

        mockMvc.perform(get("/transaction_history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))
                .andExpect(jsonPath("$.petName").value("Tommy"));
    }

    @Test
    @DisplayName("GET /transaction_history/{id} not found returns 404")
    void getById_notFound_returns404() throws Exception {
        when(transactionService.getById(99)).thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(get("/transaction_history/99"))
                .andExpect(status().isNotFound());
    }

   

    @Test
    @DisplayName("POST /transaction_history/add valid returns 200")
    void add_valid_returns200() throws Exception {
        when(transactionService.add(any(TransactionDTO.class))).thenReturn(
                buildTransaction(3, 1, "Ayon Saha", 1, "Tommy", new BigDecimal("45000.00"), "Success", "2026-03-28"));

        mockMvc.perform(post("/transaction_history/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTransactionPostBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(3))
                .andExpect(jsonPath("$.amount").value(45000.0));
    }
}
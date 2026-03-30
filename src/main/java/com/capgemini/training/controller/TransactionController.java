package com.capgemini.training.controller;

import com.capgemini.training.dto.TransactionDTO;
import com.capgemini.training.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transaction_history")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping
    public List<TransactionDTO> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maxDate) {
        return service.getAll(minDate, maxDate);
    }

    @GetMapping("/by_customer/{customerId}")
    public List<TransactionDTO> getByCustomer(@PathVariable int customerId) {
        return service.getByCustomer(customerId);
    }

    @GetMapping("/success")
    public List<TransactionDTO> getSuccess() {
        return service.getSuccessTransactions();
    }

    @GetMapping("/failed")
    public List<TransactionDTO> getFailed() {
        return service.getFailedTransactions();
    }

    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/add")
    public TransactionDTO add(@RequestBody TransactionDTO dto) {
        return service.add(dto);
    }
}
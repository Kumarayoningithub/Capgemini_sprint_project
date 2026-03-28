package com.capgemini.training.controller;

import com.capgemini.training.dto.CategoryDTO;
import com.capgemini.training.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    
    @GetMapping
    public List<CategoryDTO> getAll() {
        return service.getAllCategories();
    }

   
    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable int id) {
        return service.getById(id);
    }

   
    @GetMapping("/name/{name}")
    public CategoryDTO getByName(@PathVariable String name) {
        return service.getByName(name);
    }

 
    @PostMapping("/add")
    public CategoryDTO add(@RequestBody CategoryDTO dto) {
        return service.add(dto);
    }

   
    @PutMapping("/update/{id}")
    public CategoryDTO update(@PathVariable int id, @RequestBody CategoryDTO dto) {
        return service.update(id, dto);
    }
}
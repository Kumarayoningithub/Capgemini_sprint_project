package com.capgemini.training.service;

import com.capgemini.training.dto.CategoryDTO;
import com.capgemini.training.entity.PetCategory;
import com.capgemini.training.repository.PetCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private PetCategoryRepository repository;

  
    public List<CategoryDTO> getAllCategories() {
        return repository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

   
    public CategoryDTO getById(int id) {
        PetCategory category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return convertToDTO(category);
    }


    public CategoryDTO getByName(String name) {
        PetCategory category = repository.findByName(name);

        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        return convertToDTO(category);
    }

   
    public CategoryDTO add(CategoryDTO dto) {
        PetCategory category = new PetCategory();

        category.setName(dto.getName());

        return convertToDTO(repository.save(category));
    }

 
    public CategoryDTO update(int id, CategoryDTO dto) {

        PetCategory category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());

        return convertToDTO(repository.save(category));
    }

   
    private CategoryDTO convertToDTO(PetCategory category) {
        CategoryDTO dto = new CategoryDTO();

        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());

        return dto;
    }
}
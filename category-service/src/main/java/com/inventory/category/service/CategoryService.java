package com.inventory.category.service;

import com.inventory.category.data.CategoryData;
import com.inventory.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    public List<CategoryResponse> getAll() {
        return CategoryData.getCategories();
    }

    public CategoryResponse getByCode(String code) {
        return CategoryData.getCategories()
                .stream()
                .filter(category -> category.code().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Category not found: " + code));
    }

    public CategoryResponse create(CategoryResponse request) {
        CategoryData.addCategory(request);
        return request;
    }
}

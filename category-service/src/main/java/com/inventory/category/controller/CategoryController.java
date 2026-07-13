package com.inventory.category.controller;

import com.inventory.category.dto.CategoryResponse;
import com.inventory.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/{code}")
    public CategoryResponse getCategoryByCode(@PathVariable String code) {
        return categoryService.getByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody CategoryResponse request) {
        return categoryService.create(request);
    }
}
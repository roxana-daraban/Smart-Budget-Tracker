package com.budget.backend.controller;

import com.budget.backend.entity.Category;
import com.budget.backend.entity.TransactionType;
import com.budget.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(required = false) TransactionType type) {

        List<Category> categories;
        if (type != null) {
            // Filtrare după tip
            categories = categoryService.getCategoriesByType(type);
        } else {
            // Toate categoriile
            categories = categoryService.getAllCategories();
        }

        return ResponseEntity.ok(categories);
    }
}

package com.budget.backend.service;

import com.budget.backend.entity.Category;
import com.budget.backend.entity.TransactionType;
import com.budget.backend.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void initializeDefaultCategories() {
        // Verifică dacă categoriile există deja
        if (categoryRepository.count() > 0) {
            return; // Categoriile sunt deja create
        }

        // Categorii pentru EXPENSE (Cheltuieli)
        createCategoryIfNotExists("Food", TransactionType.EXPENSE);
        createCategoryIfNotExists("Rent", TransactionType.EXPENSE);
        createCategoryIfNotExists("Transport", TransactionType.EXPENSE);
        createCategoryIfNotExists("Utilities", TransactionType.EXPENSE);
        createCategoryIfNotExists("Shopping", TransactionType.EXPENSE);
        createCategoryIfNotExists("Entertainment", TransactionType.EXPENSE);
        createCategoryIfNotExists("Healthcare", TransactionType.EXPENSE);
        createCategoryIfNotExists("Education", TransactionType.EXPENSE);
        createCategoryIfNotExists("Other Expenses", TransactionType.EXPENSE);

        // Categorii pentru INCOME (Venituri)
        createCategoryIfNotExists("Salary", TransactionType.INCOME);
        createCategoryIfNotExists("Freelance", TransactionType.INCOME);
        createCategoryIfNotExists("Investment", TransactionType.INCOME);
        createCategoryIfNotExists("Gift", TransactionType.INCOME);
        createCategoryIfNotExists("Other Income", TransactionType.INCOME);
    }
    private void createCategoryIfNotExists(String name, TransactionType type) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            category.setType(type);
            categoryRepository.save(category);
        }
    }
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByType(TransactionType type) {
        return categoryRepository.findByType(type);
    }

}

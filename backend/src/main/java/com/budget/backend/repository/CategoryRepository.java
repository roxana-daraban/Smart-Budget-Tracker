package com.budget.backend.repository;

import com.budget.backend.entity.Category;
import com.budget.backend.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByType(TransactionType type);
    boolean existsByName(String name);

}

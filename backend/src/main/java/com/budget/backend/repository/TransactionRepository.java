package com.budget.backend.repository;

import com.budget.backend.dto.response.CategoryExpenseDTO;
import com.budget.backend.entity.Transaction;
import com.budget.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :start AND :end AND t.category.type = com.budget.backend.entity.TransactionType.INCOME")
    BigDecimal sumIncomeByUserAndDateBetween(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :start AND :end AND t.category.type = com.budget.backend.entity.TransactionType.EXPENSE")
    BigDecimal sumExpenseByUserAndDateBetween(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT new com.budget.backend.dto.response.CategoryExpenseDTO(c.name, c.id, SUM(t.amount)) FROM Transaction t JOIN t.category c WHERE t.user = :user AND t.date BETWEEN :start AND :end AND c.type = com.budget.backend.entity.TransactionType.EXPENSE GROUP BY c.id, c.name")
    List<CategoryExpenseDTO> getExpensesByCategory(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);

}

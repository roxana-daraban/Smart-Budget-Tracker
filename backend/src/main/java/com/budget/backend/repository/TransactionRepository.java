package com.budget.backend.repository;

import com.budget.backend.entity.Transaction;
import com.budget.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserOrderByDateDesc(User user);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);


}

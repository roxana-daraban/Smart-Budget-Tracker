package com.budget.backend.service;


import com.budget.backend.dto.response.CategoryExpenseDTO;
import com.budget.backend.dto.response.DashboardStatisticsDTO;
import com.budget.backend.entity.User;
import com.budget.backend.repository.TransactionRepository;
import com.budget.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatisticsDTO getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BigDecimal totalIncome = transactionRepository.sumIncomeByUserAndDateBetween(user, startDate, endDate);
        BigDecimal totalExpense = transactionRepository.sumExpenseByUserAndDateBetween(user, startDate, endDate);
        BigDecimal balance = totalIncome.subtract(totalExpense);
        List<CategoryExpenseDTO> expensesByCategory = transactionRepository.getExpensesByCategory(user, startDate, endDate);

        return DashboardStatisticsDTO.builder()
                .totalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO)
                .totalExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO)
                .balance(balance != null ? balance : BigDecimal.ZERO)
                .expensesByCategory(expensesByCategory)
                .build();
    }
}

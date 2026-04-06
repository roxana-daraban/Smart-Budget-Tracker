package com.budget.backend.controller;

import com.budget.backend.dto.request.CreateTransactionDTO;
import com.budget.backend.dto.response.TransactionResponseDTO;
import com.budget.backend.security.SecurityUtils;
import com.budget.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody CreateTransactionDTO request) {
        Long userId = requireUserId();
        TransactionResponseDTO response = transactionService.createTransaction(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        Long userId = requireUserId();
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable Long id) {
        Long userId = requireUserId();
        TransactionResponseDTO transaction = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(transaction);
    }
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody CreateTransactionDTO request) {
        Long userId = requireUserId();
        TransactionResponseDTO response = transactionService.updateTransaction(id, request, userId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id) {
        Long userId = requireUserId();
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    private static Long requireUserId() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userId;
    }
}

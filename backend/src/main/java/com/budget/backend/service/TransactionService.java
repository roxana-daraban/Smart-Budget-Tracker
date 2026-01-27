package com.budget.backend.service;

import com.budget.backend.dto.request.CreateTransactionDTO;
import com.budget.backend.dto.response.TransactionResponseDTO;
import com.budget.backend.entity.Category;
import com.budget.backend.entity.Transaction;
import com.budget.backend.entity.User;
import com.budget.backend.repository.CategoryRepository;
import com.budget.backend.repository.TransactionRepository;
import com.budget.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public TransactionResponseDTO createTransaction(CreateTransactionDTO request, Long userId) {
        // 1. Găsește utilizatorul
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Găsește categoria
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 3. Creează Transaction entity
        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDate(request.getDate());
        transaction.setUser(user);
        transaction.setCategory(category);

        // 4. Salvează în baza de date
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 5. Returnează DTO
        return convertToDTO(savedTransaction);
    }
    public List<TransactionResponseDTO> getAllTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public TransactionResponseDTO getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Business Rule: A user must only see their own transactions
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        return convertToDTO(transaction);
    }

    public TransactionResponseDTO updateTransaction(Long transactionId, CreateTransactionDTO request, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Business Rule: A user must only update their own transactions
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        // Actualizează categoria dacă este diferită
        if (!transaction.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            transaction.setCategory(category);
        }

        // Actualizează câmpurile
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDate(request.getDate());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToDTO(updatedTransaction);
    }
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Business Rule: A user must only delete their own transactions
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        transactionRepository.delete(transaction);
    }
    private TransactionResponseDTO convertToDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setDate(transaction.getDate());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setCategoryName(transaction.getCategory().getName());
        dto.setCategoryType(transaction.getCategory().getType().name());
        dto.setUserId(transaction.getUser().getId());
        return dto;
    }
}

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

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private CurrencyConversionService currencyConversionService;

    public TransactionResponseDTO createTransaction(CreateTransactionDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDate(request.getDate());
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmountInBaseCurrency(computeAmountInBase(user, request.getAmount(), request.getCurrency(), request.getDate()));

        Transaction savedTransaction = transactionRepository.save(transaction);

        return convertToDTO(savedTransaction);
    }

    public List<TransactionResponseDTO> getAllTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDescIdDesc(user);

        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        return convertToDTO(transaction);
    }

    public TransactionResponseDTO updateTransaction(Long transactionId, CreateTransactionDTO request, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        User user = transaction.getUser();

        if (!transaction.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            transaction.setCategory(category);
        }

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDate(request.getDate());
        transaction.setAmountInBaseCurrency(computeAmountInBase(user, request.getAmount(), request.getCurrency(), request.getDate()));

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToDTO(updatedTransaction);
    }

    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaction not found");
        }

        transactionRepository.delete(transaction);
    }

    private BigDecimal computeAmountInBase(User user, BigDecimal amount, String transactionCurrency, LocalDate transactionDate) {
        String base = (user.getBaseCurrency() != null && !user.getBaseCurrency().isBlank())
                ? user.getBaseCurrency().trim().toUpperCase()
                : "RON";
        return currencyConversionService.convertAmount(amount, transactionCurrency, base, transactionDate);
    }

    /**
     * Pentru API/DTO: dacă conversia în moneda de bază lipsește (0 sau null), expunem amount.
     */
    private BigDecimal effectiveAmountInBaseForResponse(Transaction transaction) {
        BigDecimal inBase = transaction.getAmountInBaseCurrency();
        if (inBase != null && inBase.compareTo(BigDecimal.ZERO) != 0) {
            return inBase;
        }
        return transaction.getAmount();
    }

    private TransactionResponseDTO convertToDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setAmountInBaseCurrency(effectiveAmountInBaseForResponse(transaction));
        dto.setDate(transaction.getDate());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setCategoryName(transaction.getCategory().getName());
        dto.setCategoryType(transaction.getCategory().getType().name());
        dto.setUserId(transaction.getUser().getId());
        return dto;
    }
}

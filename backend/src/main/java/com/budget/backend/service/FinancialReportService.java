package com.budget.backend.service;

import com.budget.backend.dto.response.CategoryExpenseDTO;
import com.budget.backend.dto.response.DashboardStatisticsDTO;
import com.budget.backend.entity.User;
import com.budget.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rapoarte financiare pe interval: reutilizează agregările din dashboard.
 */
@Service
public class FinancialReportService {

    private final UserRepository userRepository;
    private final DashboardService dashboardService;

    public FinancialReportService(UserRepository userRepository, DashboardService dashboardService) {
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
    }

    public DashboardStatisticsDTO getStatisticsForPeriod(Long userId, LocalDate from, LocalDate to) {
        return dashboardService.getStatistics(userId, from, to);
    }

    /**
     * Construiește promptul pentru Gemini (limba cerută în specificație).
     */
    public String buildAiPrompt(Long userId, LocalDate from, LocalDate to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String baseCurrency = (user.getBaseCurrency() != null && !user.getBaseCurrency().isBlank())
                ? user.getBaseCurrency().trim()
                : "RON";

        DashboardStatisticsDTO stats = dashboardService.getStatistics(userId, from, to);

        BigDecimal income = stats.getTotalIncome() != null ? stats.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal expenses = stats.getTotalExpense() != null ? stats.getTotalExpense() : BigDecimal.ZERO;
        String categoriesText = formatTopExpenseCategories(stats.getExpensesByCategory());

        return String.format(
                "Ești un expert financiar. Analizează aceste date: Moneda %s, Venituri %s, Cheltuieli %s, Top Categorii %s. "
                        + "Oferă 3 sfaturi concrete sub formă de listă și o scurtă concluzie.",
                baseCurrency,
                income.toPlainString(),
                expenses.toPlainString(),
                categoriesText
        );
    }

    private String formatTopExpenseCategories(List<CategoryExpenseDTO> expensesByCategory) {
        if (expensesByCategory == null || expensesByCategory.isEmpty()) {
            return "— (fără cheltuieli pe categorii în perioadă)";
        }
        return expensesByCategory.stream()
                .sorted(Comparator.comparing(CategoryExpenseDTO::getTotalAmount,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(c -> {
                    String name = c.getCategoryName() != null ? c.getCategoryName() : "?";
                    BigDecimal amt = c.getTotalAmount() != null ? c.getTotalAmount() : BigDecimal.ZERO;
                    return name + ": " + amt.toPlainString();
                })
                .collect(Collectors.joining("; "));
    }
}

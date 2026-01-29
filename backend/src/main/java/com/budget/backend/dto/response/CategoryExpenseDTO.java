package com.budget.backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpenseDTO {
    private String categoryName;
    private Long categoryId;
    private BigDecimal totalAmount;
}

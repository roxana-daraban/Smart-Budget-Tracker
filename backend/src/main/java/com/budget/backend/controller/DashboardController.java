package com.budget.backend.controller;

import com.budget.backend.dto.response.DashboardStatisticsDTO;
import com.budget.backend.security.SecurityUtils;
import com.budget.backend.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsDTO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        LocalDate start = from != null ? from : YearMonth.now().atDay(1);
        LocalDate end = to != null ? to : YearMonth.now().atEndOfMonth();
        return ResponseEntity.ok(dashboardService.getStatistics(userId, start, end));
    }
}

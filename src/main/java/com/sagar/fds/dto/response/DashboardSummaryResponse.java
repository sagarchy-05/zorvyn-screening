package com.sagar.fds.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
	private BigDecimal totalIncome;
	private BigDecimal totalExpenses;
	private BigDecimal netBalance;
	private long totalRecords;
	private List<CategoryTotalResponse> categoryTotals;
	private List<MonthlyTrendResponse> monthlyTrends;
	private List<RecordResponse> recentActivity;
}
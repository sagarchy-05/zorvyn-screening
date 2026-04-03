package com.sagar.fds.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sagar.fds.dto.response.CategoryTotalResponse;
import com.sagar.fds.dto.response.DashboardSummaryResponse;
import com.sagar.fds.dto.response.MonthlyTrendResponse;
import com.sagar.fds.dto.response.RecordResponse;
import com.sagar.fds.entity.enums.RecordType;
import com.sagar.fds.mapper.RecordMapper;
import com.sagar.fds.repository.FinancialRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final FinancialRecordRepository recordRepository;

	@Transactional(readOnly = true)
	public DashboardSummaryResponse getSummary() {
		BigDecimal totalIncome = recordRepository.sumByType(RecordType.INCOME);
		BigDecimal totalExpense = recordRepository.sumByType(RecordType.EXPENSE);
		BigDecimal netBalance = totalIncome.subtract(totalExpense);

		List<CategoryTotalResponse> categoryTotals = recordRepository.getCategoryTotals();

		// Last 12 months of trends
		List<MonthlyTrendResponse> monthlyTrends = recordRepository
				.getMonthlyTrendsSince(LocalDate.now().minusMonths(12)).stream()
				.map(row -> new MonthlyTrendResponse(((Number) row[0]).intValue(), ((Number) row[1]).intValue(),
						(BigDecimal) row[2], (BigDecimal) row[3], ((BigDecimal) row[2]).subtract((BigDecimal) row[3])))
				.toList();

		// 10 most recent entries
		List<RecordResponse> recentActivity = recordRepository.findRecentRecords(10).stream()
				.map(RecordMapper::toResponse).toList();

		return DashboardSummaryResponse.builder().totalIncome(totalIncome).totalExpenses(totalExpense)
				.netBalance(netBalance).totalRecords(recordRepository.countActive()).categoryTotals(categoryTotals)
				.monthlyTrends(monthlyTrends).recentActivity(recentActivity).build();
	}
}
package com.sagar.fds.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyTrendResponse {
	private int year;
	private int month;
	private BigDecimal income;
	private BigDecimal expenses;
	private BigDecimal net;
}
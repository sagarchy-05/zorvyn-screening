package com.sagar.fds.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryTotalResponse {
	private String category;
	private String type;
	private BigDecimal total;
}
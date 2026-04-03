package com.sagar.fds.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sagar.fds.entity.enums.RecordType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRecordRequest {
	@NotNull
	@DecimalMin(value = "0.01", message = "Amount must be greater than zero")
	private BigDecimal amount;

	@NotNull(message = "Type is required (INCOME or EXPENSE)")
	private RecordType type;

	@NotBlank
	@Size(max = 50)
	private String category;

	@NotNull(message = "Record date is required")
	private LocalDate recordDate;

	@Size(max = 500)
	private String description;
}
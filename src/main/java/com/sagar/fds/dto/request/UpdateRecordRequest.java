package com.sagar.fds.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sagar.fds.entity.enums.RecordType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRecordRequest {
	@DecimalMin(value = "0.01")
	private BigDecimal amount;

	private RecordType type;

	@Size(max = 50)
	private String category;

	private LocalDate recordDate;

	@Size(max = 500)
	private String description;
}
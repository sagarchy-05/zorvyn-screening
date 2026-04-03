package com.sagar.fds.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.sagar.fds.entity.enums.RecordType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordResponse {
	private Long id;
	private BigDecimal amount;
	private RecordType type;
	private String category;
	private LocalDate recordDate;
	private String description;
	private String createdByUsername;
	private Instant createdAt;
	private Instant updatedAt;
}
package com.sagar.fds.mapper;

import com.sagar.fds.dto.request.CreateRecordRequest;
import com.sagar.fds.dto.response.RecordResponse;
import com.sagar.fds.entity.FinancialRecord;
import com.sagar.fds.entity.User;

public final class RecordMapper {
	private RecordMapper() {
	}

	public static FinancialRecord toEntity(CreateRecordRequest req, User creator) {
		return FinancialRecord.builder().amount(req.getAmount()).type(req.getType()).category(req.getCategory().trim())
				.recordDate(req.getRecordDate()).description(req.getDescription()).createdBy(creator).build();
	}

	public static RecordResponse toResponse(FinancialRecord record) {
		return RecordResponse.builder().id(record.getId()).amount(record.getAmount()).type(record.getType())
				.category(record.getCategory()).recordDate(record.getRecordDate()).description(record.getDescription())
				.createdByUsername(record.getCreatedBy().getUsername()).createdAt(record.getCreatedAt())
				.updatedAt(record.getUpdatedAt()).build();
	}
}
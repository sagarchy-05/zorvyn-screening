package com.sagar.fds.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sagar.fds.dto.request.CreateRecordRequest;
import com.sagar.fds.dto.request.UpdateRecordRequest;
import com.sagar.fds.dto.response.RecordResponse;
import com.sagar.fds.entity.FinancialRecord;
import com.sagar.fds.entity.User;
import com.sagar.fds.entity.enums.RecordType;
import com.sagar.fds.exception.ResourceNotFoundException;
import com.sagar.fds.mapper.RecordMapper;
import com.sagar.fds.repository.FinancialRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

	private final FinancialRecordRepository recordRepository;

	@Transactional
	public RecordResponse create(CreateRecordRequest request, User currentUser) {
		FinancialRecord record = RecordMapper.toEntity(request, currentUser);
		return RecordMapper.toResponse(recordRepository.save(record));
	}

	@Transactional(readOnly = true)
	public RecordResponse getById(Long id) {
		return RecordMapper.toResponse(findActiveById(id));
	}

	@Transactional(readOnly = true)
	public Page<RecordResponse> getAll(RecordType type, String category, LocalDate startDate, LocalDate endDate,
			Pageable pageable) {
		return recordRepository.findWithFilters(type, category, startDate, endDate, pageable)
				.map(RecordMapper::toResponse);
	}

	@Transactional
	public RecordResponse update(Long id, UpdateRecordRequest request) {
		FinancialRecord record = findActiveById(id);

		if (request.getAmount() != null)
			record.setAmount(request.getAmount());
		if (request.getType() != null)
			record.setType(request.getType());
		if (request.getCategory() != null)
			record.setCategory(request.getCategory().trim());
		if (request.getRecordDate() != null)
			record.setRecordDate(request.getRecordDate());
		if (request.getDescription() != null)
			record.setDescription(request.getDescription());

		return RecordMapper.toResponse(recordRepository.save(record));
	}

	@Transactional
	public void softDelete(Long id) {
		FinancialRecord record = findActiveById(id);
		record.softDelete();
		recordRepository.save(record);
	}

	private FinancialRecord findActiveById(Long id) {
		return recordRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("FinancialRecord", id));
	}
}
package com.sagar.fds.controller;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sagar.fds.dto.request.CreateRecordRequest;
import com.sagar.fds.dto.request.UpdateRecordRequest;
import com.sagar.fds.dto.response.RecordResponse;
import com.sagar.fds.entity.User;
import com.sagar.fds.entity.enums.RecordType;
import com.sagar.fds.service.FinancialRecordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class FinancialRecordController {

	private final FinancialRecordService recordService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RecordResponse create(@Valid @RequestBody CreateRecordRequest request,
			@AuthenticationPrincipal User currentUser) {
		return recordService.create(request, currentUser);
	}

	@GetMapping("/{id}")
	public RecordResponse getById(@PathVariable Long id) {
		return recordService.getById(id);
	}

	@GetMapping
	public Page<RecordResponse> list(@RequestParam(required = false) RecordType type,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@ParameterObject Pageable pageable) {

		return recordService.getAll(type, category, startDate, endDate, pageable);
	}

	@PutMapping("/{id}")
	public RecordResponse update(@PathVariable Long id, @Valid @RequestBody UpdateRecordRequest request) {
		return recordService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		recordService.softDelete(id);
	}
}
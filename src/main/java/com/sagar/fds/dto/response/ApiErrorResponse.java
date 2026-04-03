package com.sagar.fds.dto.response;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
	private int status;
	private String error;
	private String message;
	private String path;
	private Instant timestamp;
	private Map<String, String> fieldErrors;
}
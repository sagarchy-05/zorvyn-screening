package com.sagar.fds.exception;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sagar.fds.dto.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), req);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), req);
	}

	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleSpringAccessDenied(
			org.springframework.security.access.AccessDeniedException ex, HttpServletRequest req) {
		return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", req);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
		return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest req) {
		Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField,
						fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value", (a, b) -> a));

		ApiErrorResponse body = ApiErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value())
				.error("Validation Failed").message("One or more fields have invalid values").path(req.getRequestURI())
				.timestamp(Instant.now()).fieldErrors(fieldErrors).build();

		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
			HttpServletRequest req) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", req);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
		log.error("Unhandled exception at {}", req.getRequestURI(), ex);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", req);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest req) {
		ApiErrorResponse body = ApiErrorResponse.builder().status(status.value()).error(status.getReasonPhrase())
				.message(message).path(req.getRequestURI()).timestamp(Instant.now()).build();
		return ResponseEntity.status(status).body(body);
	}
}
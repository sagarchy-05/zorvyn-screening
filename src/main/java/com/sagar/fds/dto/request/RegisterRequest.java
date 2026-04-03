package com.sagar.fds.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank
	@Size(min = 3, max = 50)
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
	private String password;
}
package com.sagar.fds.dto.response;

import java.time.Instant;

import com.sagar.fds.entity.enums.Role;
import com.sagar.fds.entity.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private Role role;
	private UserStatus status;
	private Instant createdAt;
}
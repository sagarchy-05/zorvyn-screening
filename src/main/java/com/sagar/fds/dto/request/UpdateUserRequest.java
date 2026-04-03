package com.sagar.fds.dto.request;

import com.sagar.fds.entity.enums.Role;
import com.sagar.fds.entity.enums.UserStatus;

import lombok.Data;

@Data
public class UpdateUserRequest {
	private Role role;
	private UserStatus status;
}
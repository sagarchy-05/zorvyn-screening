package com.sagar.fds.mapper;

import com.sagar.fds.dto.response.UserResponse;
import com.sagar.fds.entity.User;

public final class UserMapper {
	private UserMapper() {
	}

	public static UserResponse toResponse(User user) {
		return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail())
				.role(user.getRole()).status(user.getStatus()).createdAt(user.getCreatedAt()).build();
	}
}
package com.sagar.fds.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sagar.fds.dto.request.UpdateUserRequest;
import com.sagar.fds.dto.response.UserResponse;
import com.sagar.fds.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

	private final UserService userService;

	@GetMapping
	public Page<UserResponse> list(@ParameterObject Pageable pageable) {

		return userService.getAllUsers(pageable);
	}

	@GetMapping("/{id}")
	public UserResponse getById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PatchMapping("/{id}")
	public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
		return userService.updateUser(id, request);
	}
}
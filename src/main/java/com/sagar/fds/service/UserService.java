package com.sagar.fds.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sagar.fds.dto.request.UpdateUserRequest;
import com.sagar.fds.dto.response.UserResponse;
import com.sagar.fds.entity.User;
import com.sagar.fds.exception.ResourceNotFoundException;
import com.sagar.fds.mapper.UserMapper;
import com.sagar.fds.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public Page<UserResponse> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable).map(UserMapper::toResponse);
	}

	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		return UserMapper.toResponse(findById(id));
	}

	@Transactional
	public UserResponse updateUser(Long id, UpdateUserRequest request) {
		User user = findById(id);
		if (request.getRole() != null)
			user.setRole(request.getRole());
		if (request.getStatus() != null)
			user.setStatus(request.getStatus());
		return UserMapper.toResponse(userRepository.save(user));
	}

	private User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
	}
}
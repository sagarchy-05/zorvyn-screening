package com.sagar.fds.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sagar.fds.dto.request.LoginRequest;
import com.sagar.fds.dto.request.RegisterRequest;
import com.sagar.fds.dto.response.AuthResponse;
import com.sagar.fds.entity.User;
import com.sagar.fds.entity.enums.Role;
import com.sagar.fds.entity.enums.UserStatus;
import com.sagar.fds.exception.DuplicateResourceException;
import com.sagar.fds.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateResourceException("Username already taken: " + request.getUsername());
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already registered: " + request.getEmail());
		}

		User user = User.builder().username(request.getUsername()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.VIEWER).status(UserStatus.ACTIVE)
				.build();

		userRepository.save(user);

		return AuthResponse.builder().token(jwtService.generateToken(user)).username(user.getUsername())
				.role(user.getRole().name()).build();
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

		return AuthResponse.builder().token(jwtService.generateToken(user)).username(user.getUsername())
				.role(user.getRole().name()).build();
	}
}
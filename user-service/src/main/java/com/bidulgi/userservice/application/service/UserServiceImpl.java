package com.bidulgi.userservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.userservice.application.dto.CreateUserRequest;
import com.bidulgi.userservice.application.dto.DeleteUserResponse;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.dto.UpdateUserRequest;
import com.bidulgi.userservice.domain.model.User;
import com.bidulgi.userservice.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserResponse createUser(CreateUserRequest request) {
		String encodedPassword = passwordEncoder.encode(request.password());

		User user = User.create(
			request.name(),
			request.nickname(),
			request.email(),
			encodedPassword,
			request.birthday(),
			request.gender(),
			request.role()
		);
		System.out.println(user.toString());
		try{

			User saved = userRepository.save(user);
			return toResponse(saved);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
		return toResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
			.map(this::toResponse)
			.toList();
	}

	@Override
	public UserResponse updateUser(UUID id, UpdateUserRequest request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

		user.updateProfile(
			request.name(),
			request.nickname(),
			request.email(),
			request.gender()
		);

		// 비밀번호 변경 요청 시에만 암호화 & 변경
		if (request.password() != null && !request.password().isBlank()) {
			String encodedPassword = passwordEncoder.encode(request.password());
			user.updatePassword(encodedPassword);
		}

		return toResponse(user);
	}

	@Override
	public DeleteUserResponse deleteUser(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

		user.markAsDeleted(id);

		return new DeleteUserResponse(id,user.getDeletedAt(),user.getDeletedBy());
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(
			user.getId(),
			user.getName(),
			user.getNickname(),
			user.getEmail(),
			user.getBirthday(),
			user.getGender(),
			user.getRole()
		);
	}
}

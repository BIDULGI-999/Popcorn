package com.bidulgi.userservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.ErrorCode;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.userservice.application.dto.CreateUserRequest;
import com.bidulgi.userservice.application.dto.DeleteUserResponse;
import com.bidulgi.userservice.application.dto.UserProfileResponse;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.dto.UpdateUserRequest;
import com.bidulgi.userservice.domain.model.User;
import com.bidulgi.userservice.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

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

		try {
			User saved = userRepository.save(user);
			return toResponse(saved);
		} catch (DataIntegrityViolationException e) {
			// 예: email unique 제약 / not null 위반 등
			throw new InternalServiceException(
				ErrorCode.INVALID_INPUT_VALUE,
				"유효하지 않은 입력 값입니다. (중복/제약조건 위반 가능)"
			);
		} catch (Exception e) {
			throw new InternalServiceException(
				ErrorCode.INTERNAL_SERVER_ERROR,
				"사용자 생성 중 서버 오류가 발생했습니다."
			);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				ErrorCode.RESOURCE_NOT_FOUND,
				"유저를 찾을 수 없습니다. id=" + id
			));
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
			.orElseThrow(() -> new EntityNotFoundException(
				ErrorCode.RESOURCE_NOT_FOUND,
				"유저를 찾을 수 없습니다. id=" + id
			));

		user.updateProfile(
			request.name(),
			request.nickname(),
			request.email(),
			request.gender()
		);

		if (request.password() != null && !request.password().isBlank()) {
			String encodedPassword = passwordEncoder.encode(request.password());
			user.updatePassword(encodedPassword);
		}

		// JPA Dirty Checking으로 flush 시점에 반영되므로 별도 save 없어도 OK
		return toResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserProfileResponse getUserProfile(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(
				ErrorCode.RESOURCE_NOT_FOUND,
				"유저를 찾을 수 없습니다. id=" + userId
			));
		return UserProfileResponse.from(user);
	}

	@Override
	public DeleteUserResponse deleteUser(UUID id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				ErrorCode.RESOURCE_NOT_FOUND,
				"유저를 찾을 수 없습니다. id=" + id
			));

		user.markAsDeleted(id);

		return new DeleteUserResponse(id, user.getDeletedAt(), user.getDeletedBy());
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

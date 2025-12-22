package com.bidulgi.userservice.application.service;

import java.util.List;
import java.util.UUID;

import com.bidulgi.userservice.application.dto.CreateUserRequest;
import com.bidulgi.userservice.application.dto.DeleteUserResponse;
import com.bidulgi.userservice.application.dto.UserProfileResponse;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.dto.UpdateUserRequest;

public interface UserService {
	UserResponse createUser(CreateUserRequest request);

	UserResponse getUserById(UUID id);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(UUID id, UpdateUserRequest request);

	UserProfileResponse getUserProfile(UUID userId);

	DeleteUserResponse deleteUser(UUID id);
}

package com.bidulgi.userservice.application.service;

import java.util.List;
import java.util.UUID;

import com.bidulgi.userservice.application.dto.UserCreateRequest;
import com.bidulgi.userservice.application.dto.UserDeleteResponse;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.dto.UserUpdateRequest;

public interface UserService {
	UserResponse createUser(UserCreateRequest request);

	UserResponse getUserById(UUID id);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(UUID id, UserUpdateRequest request);

	UserDeleteResponse deleteUser(UUID id);
}

package com.bidulgi.userservice.presentation;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.userservice.application.dto.CreateUserRequest;
import com.bidulgi.userservice.application.dto.DeleteUserResponse;
import com.bidulgi.userservice.application.dto.UpdateUserRequest;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ApiResponse<UserResponse> create(@RequestBody CreateUserRequest request) {
		UserResponse response = userService.createUser(request);
		return ApiResponse.success(response, "success");
	}

	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getUserById(@PathVariable UUID id) {
		UserResponse response = userService.getUserById(id);
		return ApiResponse.success(response, "success");
	}

	@GetMapping
	public ApiResponse<List<UserResponse>> getAllUsers() {
		List<UserResponse> responses = userService.getAllUsers();
		return ApiResponse.success(responses, "success");
	}

	@PutMapping("/{id}")
	public ApiResponse<UserResponse> update(
		@PathVariable UUID id,
		@RequestBody UpdateUserRequest request
	) {
		UserResponse response = userService.updateUser(id, request);
		return ApiResponse.success(response,"success");
	}

	@DeleteMapping("/{id}")
	public ApiResponse<DeleteUserResponse> delete(@PathVariable UUID id) {
		DeleteUserResponse response = userService.deleteUser(id);
		return ApiResponse.success(response, "success");
	}
}

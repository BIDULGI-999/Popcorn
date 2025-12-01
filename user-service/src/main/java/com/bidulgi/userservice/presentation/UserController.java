package com.bidulgi.userservice.presentation;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.userservice.application.dto.UserCreateRequest;
import com.bidulgi.userservice.application.dto.UserDeleteResponse;
import com.bidulgi.userservice.application.dto.UserUpdateRequest;
import com.bidulgi.userservice.application.dto.UserResponse;
import com.bidulgi.userservice.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public ApiResponse<UserResponse> create(@RequestBody UserCreateRequest request) {
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
		@RequestBody UserUpdateRequest request
	) {
		UserResponse response = userService.updateUser(id, request);
		return ApiResponse.success(response,"success");
	}

	@DeleteMapping("/{id}")
	public ApiResponse<UserDeleteResponse> delete(@PathVariable UUID id) {
		UserDeleteResponse response = userService.deleteUser(id);
		return ApiResponse.success(response, "success");
	}
}

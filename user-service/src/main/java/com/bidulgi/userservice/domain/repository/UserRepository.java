package com.bidulgi.userservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bidulgi.userservice.domain.model.User;

public interface UserRepository {

	User save(User user);

	Optional<User> findById(UUID id);

	List<User> findAll();

	void deleteById(UUID id);
}

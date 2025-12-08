package com.bidulgi.userservice.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.userservice.domain.model.User;

public interface UserJpaRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);
}

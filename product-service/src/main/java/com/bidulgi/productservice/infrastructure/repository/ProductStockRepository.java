package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.ProductStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductStock p where p.id = :id")
    Optional<ProductStock> findByIdForUpdate(@Param("id") UUID id);
}
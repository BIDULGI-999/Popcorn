package com.bidulgi.common.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

	@CreatedDate
	@Column(updatable = false, nullable = false)
	protected LocalDateTime createdAt;

	@LastModifiedDate
	protected LocalDateTime updatedAt;

	protected LocalDateTime deletedAt;

	@CreatedBy
	@Column(updatable = false, nullable = false)
	protected UUID createdBy;

	@LastModifiedBy
	protected UUID updatedBy;

	protected UUID deletedBy;

	public void markAsDeleted(UUID actor) {
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = actor;
	}

	public void restore() {
		this.deletedAt = null;
		this.deletedBy = null;
	}
}
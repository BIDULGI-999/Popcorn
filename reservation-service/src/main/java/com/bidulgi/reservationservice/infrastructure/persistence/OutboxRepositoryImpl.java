package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.outbox.Outbox;
import com.bidulgi.reservationservice.domain.model.outbox.OutboxStatus;
import com.bidulgi.reservationservice.domain.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OutboxRepositoryImpl implements OutboxRepository {

	private final OutboxJpaRepository jpaRepository;

	@Override
	public void save(Outbox outbox) {
		jpaRepository.save(outbox);
	}

	@Override
	public Optional<Outbox> findByEventId(String eventId) {
		return jpaRepository.findByEventId(eventId);
	}

	@Override
	public List<Outbox> findByStatusIn(Collection<OutboxStatus> statuses) {
		return jpaRepository.findByStatusIn(statuses);
	}
}

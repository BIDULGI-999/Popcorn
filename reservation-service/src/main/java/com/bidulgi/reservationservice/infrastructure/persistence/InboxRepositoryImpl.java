package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.inbox.Inbox;
import com.bidulgi.reservationservice.domain.model.inbox.InboxStatus;
import com.bidulgi.reservationservice.domain.repository.InboxRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class InboxRepositoryImpl implements InboxRepository {

	private final InboxJpaRepository jpaRepository;

	@Override
	public void save(Inbox inbox) {
		jpaRepository.save(inbox);
	}

	@Override
	public Optional<Inbox> findById(Long inboxId) {
		return jpaRepository.findById(inboxId);
	}

	@Override
	public boolean existsByEventId(String eventId) {
		return jpaRepository.existsByEventId(eventId);
	}

	@Override
	public List<Inbox> findTop100ByStatusOrderByCreatedAtAsc(InboxStatus inboxStatus) {
		return jpaRepository.findTop100ByStatusOrderByCreatedAtAsc(inboxStatus);
	}
}

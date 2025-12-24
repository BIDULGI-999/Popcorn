package com.bidulgi.reservationservice.domain.repository;

import java.util.List;
import java.util.Optional;

import com.bidulgi.reservationservice.domain.model.inbox.Inbox;
import com.bidulgi.reservationservice.domain.model.inbox.InboxStatus;

public interface InboxRepository {
	void save(Inbox inbox);
	Optional<Inbox> findById(Long inboxId);
	boolean existsByEventId(String eventId);
	List<Inbox> findTop100ByStatusOrderByCreatedAtAsc(InboxStatus inboxStatus);
}

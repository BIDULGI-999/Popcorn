package com.bidulgi.reservationservice.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.bidulgi.reservationservice.domain.model.outbox.Outbox;
import com.bidulgi.reservationservice.domain.model.outbox.OutboxStatus;

public interface OutboxRepository {
	void save(Outbox outbox);
	Optional<Outbox> findByEventId(String eventId);
	List<Outbox> findByStatusIn(Collection<OutboxStatus> statuses);
}

package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.outbox.Outbox;
import com.bidulgi.reservationservice.domain.model.outbox.OutboxStatus;

@Repository
public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
	Optional<Outbox> findByEventId(String eventId);
	List<Outbox> findByStatusIn(Collection<OutboxStatus> statuses);
}

package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.inbox.Inbox;
import com.bidulgi.reservationservice.domain.model.inbox.InboxStatus;

@Repository
public interface InboxJpaRepository extends JpaRepository<Inbox, Long> {
	boolean existsByEventId(String eventId);
	List<Inbox> findTop100ByStatusOrderByCreatedAtAsc(InboxStatus inboxStatus);
}

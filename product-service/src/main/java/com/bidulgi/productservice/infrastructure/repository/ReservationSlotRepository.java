package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.ReservationSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, UUID> {
    List<ReservationSlot> findAllByProductPeriod_IdAndSlotDate(UUID productPeriodId, LocalDate slotDate);

    // [Perssimistic Lock이 필요할 경우를 대비한 예시]
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // @Query("select s from ReservationSlot s where s.id = :id")
    // Optional<ReservationSlot> findByIdWithLock(@Param("id") UUID id);
}

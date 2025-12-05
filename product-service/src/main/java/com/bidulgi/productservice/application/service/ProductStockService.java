package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.domain.entity.ReservationSlot;
import com.bidulgi.productservice.infrastructure.repository.ReservationSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ReservationSlotRepository reservationSlotRepository;

    /**
     * 재고 차감 (낙관적 락 적용)
     * 동시성 이슈 발생 시 ObjectOptimisticLockingFailureException 발생 -> ControllerAdvice에서 처리 필요
     */
    @Transactional
    public void decreaseStock(UUID slotId, int count) {
        ReservationSlot slot = reservationSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("슬롯을 찾을 수 없습니다."));

        // 비즈니스 로직 검증: 예약 가능 여부 확인
        if (!slot.getIsAvailable()) {
            throw new IllegalStateException("해당 슬롯은 예약이 마감되었습니다.");
        }

        // 비즈니스 로직 검증 : 잔여 좌석 확인
        if (slot.getCurrentCount() + count > slot.getMaxCapacity()) {
            throw new IllegalStateException("잔여 좌석이 부족합니다.");
        }

        // 상태 변경 (Dirty Checking에 의해 update 쿼리 실행)
        // version 필드가 자동으로 체크됨
        // slot.increaseReservation(count); // Entity 메서드 호출
        // Entity 메서드 직접 구현 가정:
        updateSlotCount(slot, count);
    }

    /**
     * 재고 복구 (Rollback)
     */
    @Transactional
    public void increaseStock(UUID slotId, int count) {
        ReservationSlot slot = reservationSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("슬롯을 찾을 수 없습니다."));

        // 단순 감소 로직 (0 미만 방지 필요)
         slot.decreaseReservation(count);
    }

    // Entity 내부 메서드로 이동 권장 (임시 구현)
    private void updateSlotCount(ReservationSlot slot, int count) {
        try {
            // 리플렉션이나 별도 세터를 사용하지 않고,
            // 실제로는 Entity 내부에 business method(increaseReservation)를 만들어 호출해야 함.
            // 여기서는 Entity 코드를 수정하지 않고 로직을 보여주기 위해 주석 설명으로 대체.
             slot.increaseReservation(count);

            // *중요*: Entity 파일의 increaseReservation 메서드를 조금 수정해야 합니다.
            // count를 인자로 받도록. (이전 코드는 1씩만 증가했음)
//            slot.increaseReservation(); // 1명씩이라 가정하거나 Entity 수정 필요
        } catch (Exception e) {
            throw new RuntimeException("Entity 메서드 호출 실패", e);
        }
    }

}

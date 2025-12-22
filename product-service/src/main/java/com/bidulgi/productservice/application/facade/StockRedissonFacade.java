package com.bidulgi.productservice.application.facade;

import com.bidulgi.productservice.application.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockRedissonFacade {

    private final RedissonClient redissonClient;
    private final ProductStockService productStockService;

    /**
     * 재고 차감 (Decrease)
     */
    public void decreaseStock(UUID slotId, int count) {
        String lockKey = "stock:" + slotId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 🚨 핵심 수정: 락 획득 대기 시간을 10초 -> 100초로 변경
            // 1000명이 몰릴 경우, 뒤에 있는 사람들은 앞사람 처리가 끝날 때까지 꽤 오래 기다려야 합니다.
            // 따라서 타임아웃을 넉넉하게 줘야 "서버 혼잡(500)" 에러 없이 "재고 부족(400)" 응답을 받을 수 있습니다.
            boolean available = lock.tryLock(100, 5, TimeUnit.SECONDS);

            if (!available) {
                log.error("[Lock 획득 실패 - 타임아웃] Thread: {}", Thread.currentThread().getName());
                throw new RuntimeException("접속자가 너무 많습니다. 잠시 후 다시 시도해주세요.");
            }

            // 성공 로그 (순서 확인용)
//            log.info("[Lock 획득 성공] Thread: {} | SlotId: {}", Thread.currentThread().getName(), slotId);

            // 비즈니스 로직 수행
            productStockService.decreaseStock(slotId, count);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 락 해제 및 반납 로그
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
//                log.info("[Lock 반납 완료] Thread: {} -------------------", Thread.currentThread().getName());
            }
        }
    }

    /**
     * 재고 복구 (Increase) - Rollback 등
     */
    public void increaseStock(UUID slotId, int count) {
        String lockKey = "stock:" + slotId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 복구 로직도 동일하게 대기 시간을 늘려줍니다.
            boolean available = lock.tryLock(100, 5, TimeUnit.SECONDS);

            if (!available) {
                log.error("[복구 Lock 획득 실패] Thread: {}", Thread.currentThread().getName());
                throw new RuntimeException("Lock 획득 실패");
            }

//            log.info("[복구 Lock 획득] Thread: {} | SlotId: {}", Thread.currentThread().getName(), slotId);

            productStockService.increaseStock(slotId, count);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
//                log.info("[복구 Lock 반납] Thread: {} -------------------", Thread.currentThread().getName());
            }
        }
    }
}

//package com.bidulgi.productservice.application.facade;
//
//import com.bidulgi.productservice.application.service.ProductStockService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class StockRedissonFacade {
//
//    private final RedissonClient redissonClient;
//    private final ProductStockService productStockService;
//
//    public void decreaseStock(UUID slotId, int count) {
//        // 1. 락 이름 정의 (고유해야 함. 예: stock:uuid)
//        String lockKey = "stock:" + slotId.toString();
//        RLock lock = redissonClient.getLock(lockKey);
//
//        try {
//            // 2. 락 획득 시도
//            // waitTime: 락을 얻기 위해 기다리는 시간 (10초)
//            // leaseTime: 락을 얻고 나서 잡고 있는 시간 (5초 지나면 자동 해제 - 데드락 방지)
//            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);
//
//            if (!available) {
//                log.error("락 획득 실패 - 트래픽 과부하");
//                throw new RuntimeException("서버가 혼잡합니다. 잠시 후 다시 시도해주세요.");
//            }
//
//            // 3. 실제 비즈니스 로직 실행 (트랜잭션은 이 내부에서 시작되고 끝남)
//            productStockService.decreaseStock(slotId, count);
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            // 4. 락 해제 (반드시 finally에서!)
//            if (lock.isHeldByCurrentThread()) { // 내가 잡은 락인지 확인 후 해제
//                lock.unlock();
//            }
//        }
//    }
//
//    // increaseStock도 동일한 패턴으로 구현하면 됩니다.
//    public void increaseStock(UUID slotId, int count) {
//        String lockKey = "stock:" + slotId.toString();
//        RLock lock = redissonClient.getLock(lockKey);
//
//        try {
//            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);
//            if (!available) throw new RuntimeException("Lock 획득 실패");
//
//            productStockService.increaseStock(slotId, count);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (lock.isHeldByCurrentThread()) lock.unlock();
//        }
//    }
//}
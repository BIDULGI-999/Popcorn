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

    public void decreaseStock(UUID slotId, int count) {
        String lockKey = "stock:" + slotId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 1. 락 획득 시도 로그
            // log.info("[Lock 시도] Thread: {}", Thread.currentThread().getName());

            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!available) {
                log.error("[Lock 획득 실패] Thread: {}", Thread.currentThread().getName());
                throw new RuntimeException("서버가 혼잡합니다.");
            }

            // 2. 락 획득 성공 로그 (이게 순차적으로 찍혀야 성공!)
            log.info("[Lock 획득 성공] Thread: {} | SlotId: {}", Thread.currentThread().getName(), slotId);

            productStockService.decreaseStock(slotId, count);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                // 3. 락 반납 로그
                log.info("[Lock 반납 완료] Thread: {} -------------------", Thread.currentThread().getName());
            }
        }
    }

    // increaseStock도 똑같이 로그를 추가해주세요!
    public void increaseStock(UUID slotId, int count) {
        String lockKey = "stock:" + slotId.toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!available) throw new RuntimeException("Lock 획득 실패");

            log.info("[복구 Lock 획득] Thread: {} | SlotId: {}", Thread.currentThread().getName(), slotId);

            productStockService.increaseStock(slotId, count);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[복구 Lock 반납] Thread: {} -------------------", Thread.currentThread().getName());
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
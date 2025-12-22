package com.bidulgi.productservice;

import com.bidulgi.productservice.application.facade.StockRedissonFacade;
import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.entity.ProductPeriod;
import com.bidulgi.productservice.domain.entity.ReservationSlot;
import com.bidulgi.productservice.infrastructure.repository.ProductPeriodRepository;
import com.bidulgi.productservice.infrastructure.repository.ProductRepository;
import com.bidulgi.productservice.infrastructure.repository.ReservationSlotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockRedissonFacadeTest {

	@Autowired
	private StockRedissonFacade stockRedissonFacade;

	@Autowired
	private ReservationSlotRepository reservationSlotRepository;

	@Autowired
	private ProductRepository productRepository; // Product 저장을 위해 추가

	// ProductPeriodRepository가 있다고 가정 (FK 제약조건 해결용)
	@Autowired
	private ProductPeriodRepository productPeriodRepository;

	private UUID slotId;

	@BeforeEach
	void setUp() {
		// 1. 최상위 부모: Product 생성 및 저장 (빌더 가정)
		Product product = Product.builder()
				.placeId(UUID.randomUUID()) // 필수 (nullable = false)
				.name("테스트 상품")         // 필수
				.price(10000L)              // 필수
				.description("테스트 상품 설명입니다.") // 필수 (nullable = false, TEXT)
				.build();
		productRepository.save(product);

		// 2. 중간 부모: ProductPeriod 생성 및 저장
		ProductPeriod period = ProductPeriod.builder()
				.product(product) // 위에서 만든 product 연결
				.name("1회차")
				.periodStart(LocalDate.now())
				.periodEnd(LocalDate.now().plusDays(1))
				.saleStartAt(LocalDateTime.now())
				.saleEndAt(LocalDateTime.now().plusDays(1))
				.build();
		productPeriodRepository.save(period);

		// 3. 자식: ReservationSlot 생성 및 저장
		ReservationSlot slot = ReservationSlot.builder()
				.productPeriod(period) // 위에서 만든 period 연결
				.slotDate(LocalDate.now())
				.slotTime(LocalTime.now())
				.maxCapacity(100L)
				.build();

		ReservationSlot savedSlot = reservationSlotRepository.save(slot);
		slotId = savedSlot.getId();
	}

	@AfterEach
	void tearDown() {
		reservationSlotRepository.deleteAll();
		productPeriodRepository.deleteAll();
	}

	@Test
	@DisplayName("100명 동시 예약 시 - 분산락 적용으로 정확히 100명이 예약되어야 한다")
	void decreaseStock_Concurrency() throws InterruptedException {
		// given
		int threadCount = 100;
		// 32개 스레드 풀 (서버 자원 흉내)
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		// 모든 스레드가 끝날 때까지 기다리는 빗장
		CountDownLatch latch = new CountDownLatch(threadCount);

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					// 1명씩 예약 (재고 차감 요청)
					stockRedissonFacade.decreaseStock(slotId, 1);
				} catch (Exception e) {
					// 예외 발생 시 로그 출력 (테스트 디버깅용)
					System.out.println("예약 실패: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await(); // 100개 요청 끝날 때까지 대기

		// then
		ReservationSlot slot = reservationSlotRepository.findById(slotId).orElseThrow();

		// 검증 1: 예약된 인원수(currentCount)가 100이어야 함
		assertThat(slot.getCurrentCount()).isEqualTo(100L);

		// 검증 2: 정원이 꽉 찼으므로 isAvailable은 false로 변해야 함
		assertThat(slot.getIsAvailable()).isFalse();
	}
}
package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.application.dto.request.CreatePeriodRequest;
import com.bidulgi.productservice.application.dto.request.GenerateSlotRequest;
import com.bidulgi.productservice.application.dto.response.PeriodResponse;
import com.bidulgi.productservice.application.dto.response.SlotResponse;
import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.entity.ProductPeriod;
import com.bidulgi.productservice.domain.entity.ReservationSlot;
import com.bidulgi.productservice.infrastructure.repository.ProductPeriodRepository;
import com.bidulgi.productservice.infrastructure.repository.ProductRepository;
import com.bidulgi.productservice.infrastructure.repository.ReservationSlotRepository;
import com.bidulgi.productservice.presentation.exception.PeriodValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPeriodService {

    private final ProductRepository productRepository;
    private final ProductPeriodRepository productPeriodRepository;
    private final ReservationSlotRepository reservationSlotRepository;

    // 회차 등록
    @Transactional
    public PeriodResponse createPeriod(UUID productId, CreatePeriodRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // [검증] 회차(Period)의 시작일이 상품(Product)의 시작일보다 빠르면 안 됨
        if (request.getPeriodStart().isBefore(product.getStartDate().toLocalDate())) {
            throw new PeriodValidationException("회차 기간은 행사 시작일 이후여야 합니다.");
        }

        // [검증] 회차 종료일이 행사 종료일보다 늦으면 안 됨
        if (request.getPeriodEnd().isAfter(product.getEndDate().toLocalDate())) {
            throw new PeriodValidationException("회차 기간은 행사 종료일 이전이어야 합니다.");
        }

        ProductPeriod period = request.toEntity(product);
        ProductPeriod savedPeriod = productPeriodRepository.save(period);
        return PeriodResponse.from(savedPeriod);
    }

    // 슬롯 대량 생성 (핵심 로직)
    @Transactional
    public int generateSlots(UUID periodId, GenerateSlotRequest request) {
        ProductPeriod period = productPeriodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("회차 정보가 없습니다."));

        // 요청 기간이 회차 기간 안에 있는지 검증
        if (request.getStartDate().isBefore(period.getPeriodStart())) {
            throw new PeriodValidationException("시작일이 회차 시작일 이전일 수 없습니다.");
        }
        if (request.getEndDate().isAfter(period.getPeriodEnd())) {
            throw new PeriodValidationException("종료일이 회차 종료일 이후일 수 없습니다.");
        }

        List<ReservationSlot> slots = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();

        // 1. 날짜 반복 (시작일 ~ 종료일)
        while (!currentDate.isAfter(request.getEndDate())) {

            // 2. 시간 반복 (시작 시간 ~ 종료 시간)
            LocalTime currentTime = request.getStartTime();
            while (!currentTime.isAfter(request.getEndTime().minusMinutes(request.getIntervalMinutes()))) {


                // 5. 중복 체크 (서비스 레벨)
                boolean exists = reservationSlotRepository.existsByProductPeriodAndSlotDateAndSlotTime(
                        period, currentDate, currentTime
                );

                if (exists) {
                    throw new PeriodValidationException(
                            String.format("중복된 슬롯이 존재합니다. 날짜: %s, 시간: %s", currentDate, currentTime)
                    );
                }

                // 중복 체크 로직이 필요하다면 여기서 수행하거나 DB Unique Key에 의존
                ReservationSlot slot = ReservationSlot.builder()
                        .productPeriod(period)
                        .slotDate(currentDate)
                        .slotTime(currentTime)
                        .maxCapacity(request.getMaxCapacity())
                        .build();

                slots.add(slot);

                // 시간 증가
                currentTime = currentTime.plusMinutes(request.getIntervalMinutes());
            }

            // 날짜 증가
            currentDate = currentDate.plusDays(1);

        }

        // Batch Insert (saveAll 은 내부적으로 배치 처리 됨 - JPA 설정 필요)
        reservationSlotRepository.saveAll(slots);

        return slots.size();
    }

    // 날짜별 슬롯 조회
    @Transactional(readOnly = true)
    public List<SlotResponse> getSlotsByDate(UUID periodId, LocalDate date) {
        return reservationSlotRepository.findAllByProductPeriod_IdAndSlotDate(periodId, date).stream()
                .map(SlotResponse::from)
                .collect(Collectors.toList());
    }
}

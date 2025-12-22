import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

// 1. 커스텀 카운터 정의 (결과창에서 보기 편하게)
const successCount = new Counter('custom_success_count');
const failCount = new Counter('custom_fail_count');

export const options = {
    vus: 100,
    iterations: 100, // 100명이 1번씩
    thresholds: {
        // 테스트 통과 기준 설정 (선택사항)
        // 예: 성공 횟수가 100번이어야 초록불(Pass)
        'custom_success_count': ['count>=100'],
    },
};

export default function () {
    const url = 'http://localhost:8300/internal/products/stock/decrease';

    // ⚠️ [중요] 실제 DB에 생성된 슬롯 ID로 교체하세요!
    const payload = JSON.stringify({
        slotId: "7fe6ed34-459b-4e69-bac9-ed9697dc586c",
        count: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    // 2. 응답 상태 체크 및 카운팅
    // 200 OK 인지 확인
    const isSuccess = check(res, {
        'Status is 200 OK': (r) => r.status === 200,
        'Status is 500 Server Error': (r) => r.status === 500, // 서버 터짐 확인
        'Status is 400 Bad Request': (r) => r.status === 400, // 재고 부족 등
    });

    if (isSuccess) {
        successCount.add(1);
    } else {
        failCount.add(1);
        // 3. 실패 시 에러 메시지 출력 (너무 많이 출력되면 보기 힘드니, 10% 확률로만 찍거나 필요시 주석 해제)
        // 실패 원인을 바로 보고 싶다면 아래 주석을 푸세요.
        console.log(`[Fail] Status: ${res.status}, Body: ${res.body}`);
    }
}
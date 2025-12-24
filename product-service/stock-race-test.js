import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

// 메트릭 정의
const successCount = new Counter('success_order_count'); // 예매 성공 (200)
const soldOutCount = new Counter('fail_sold_out_count'); // 재고 부족 (400)
const errorCount = new Counter('fail_server_error_count'); // 서버 폭발 (500/Timeout)

export const options = {
    // ⚠️ 1000명이 동시에 접속!
    vus: 1000,
    iterations: 1000, // 총 1000번 요청

    // [검증 목표: 100개 팔고 900명 막기]
    thresholds: {
        'success_order_count': ['count==100'], // 정확히 100개만 팔려야 함
        'fail_sold_out_count': ['count==900'], // 정확히 900명은 '재고 부족' 떠야 함
    },
    // 1000명이 몰리면 타임아웃 날 수 있으니 넉넉하게 설정
    timeout: '2m',
};

export default function () {
    const url = 'http://localhost:8300/internal/products/stock/decrease';

    // ⚠️ UUID 확인!
    const payload = JSON.stringify({
        slotId: "7fe6ed34-459b-4e69-bac9-ed9697dc586c",
        count: 1
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
        timeout: '60s' // 요청 타임아웃 60초 (대기열 감안)
    };

    try {
        const res = http.post(url, payload, params);

        if (res.status === 200) {
            successCount.add(1);
        } else if (res.status === 400) {
            soldOutCount.add(1);
        } else {
            errorCount.add(1);
            // 500 에러나 타임아웃이 발생하면 로그 출력
            console.error(`🚨 서버 에러! Status: ${res.status}, Body: ${res.body}`);
        }

        check(res, {
            '📦 Success (200)': (r) => r.status === 200,
            '🛡️ Sold Out (400)': (r) => r.status === 400,
        });

    } catch (e) {
        // k6 자체 타임아웃 등
        errorCount.add(1);
        console.error(`💀 Request Failed: ${e}`);
    }
}
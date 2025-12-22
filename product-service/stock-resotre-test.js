import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';

const successCount = new Counter('custom_success_count');
const failCount = new Counter('custom_fail_count');

export const options = {
    vus: 100,       // 100명 동시 접속
    iterations: 100, // 총 100번 수행
    thresholds: {
        'custom_success_count': ['count>=100'], // 100개 다 성공해야 합격
    },
};

export default function () {
    // ⚠️ URL이 increase로 변경되었습니다.
    const url = 'http://localhost:8300/internal/products/stock/increase';

    // ⚠️ 아까와 동일한 슬롯 ID를 사용하세요 (지금 100명이 차있는 그 슬롯)
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

    // 성공 판단 로직
    const isSuccess = res.status === 200;

    check(res, {
        'Status is 200 OK': (r) => r.status === 200,
    });

    if (isSuccess) {
        successCount.add(1);
    } else {
        failCount.add(1);
        console.log(`[Fail] Status: ${res.status}, Body: ${res.body}`);
    }
}
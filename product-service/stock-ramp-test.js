import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// 1. 메트릭 정의 (결과창에서 보기 편하게)
const successCount = new Counter('success_order_count'); // 성공 (200)
const soldOutCount = new Counter('fail_sold_out_count'); // 품절 방어 (400)
const errorCount = new Counter('fail_server_error_count'); // 에러 (500, Timeout)

export const options = {
    // 2. 램프업 시나리오 설정
    scenarios: {
        ramp_up_scenario: {
            executor: 'ramping-vus', // 유저 수를 조절하는 실행기
            startVUs: 0,             // 0명에서 시작
            stages: [
                { duration: '10s', target: 1000 }, // 10초 동안 1000명까지 증가
                { duration: '5s', target: 1000 },  // 5초 동안 1000명 유지
                { duration: '5s', target: 0 },     // 5초 동안 0명으로 감소 (정리)
            ],
            gracefulRampDown: '0s',
        },
    },

    // 3. 검증 목표
    thresholds: {
        'success_order_count': ['count==100'], // 정확히 100개 판매
        // 램프업 방식은 루프 때문에 요청 총량이 1000개를 살짝 넘을 수 있어서 >= 부등호 사용
        'fail_sold_out_count': ['count>=900'],
    },
    timeout: '2m',
};

export default function () {
    const url = 'http://localhost:8300/internal/products/stock/decrease';

    // ⚠️ UUID 본인 것으로 확인 필수!
    const payload = JSON.stringify({
        slotId: "7fe6ed34-459b-4e69-bac9-ed9697dc586c",
        count: 1
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
        timeout: '60s' // 대기열 고려하여 넉넉히
    };

    try {
        const res = http.post(url, payload, params);

        // 4. 상태 코드에 따른 분기 처리 및 로그
        if (res.status === 200) {
            successCount.add(1);
        } else if (res.status === 400) {
            soldOutCount.add(1);
        } else {
            // 500 에러나 기타 상태 코드일 때만 로그 출력
            errorCount.add(1);
            console.error(`🚨 서버 에러! Status: ${res.status}, Body: ${res.body}`);
        }

        // 5. 체크 (결과 요약용)
        check(res, {
            '📦 Success (200)': (r) => r.status === 200,
            '🛡️ Sold Out (400)': (r) => r.status === 400,
        });

    } catch (e) {
        errorCount.add(1);
        console.error(`💀 Request Failed: ${e}`);
    }

    // 💡 중요: 램프업 시 한 유저가 너무 빨리 재요청해서 요청 폭탄(4000건 등)이 되는 걸 방지
    // 1초 정도 쉬었다가 다음 요청을 보내도록 조절
    sleep(1);
}
import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "10s", target: 500 }, 
    { duration: "50s", target: 500 }, 
  ],
};

// ✅ 테스트 시작 전에 한 번만 실행 (모든 사용자 토큰 발급)
export function setup() {
  const queueTokenUrl = "http://localhost:8080/queue_tokens";
  const users = [];

  for (let i = 1; i <= 500; i++) {
    const payload = JSON.stringify({ userId: i });
    const params = { headers: { "Content-Type": "application/json" } };
    const res = http.post(queueTokenUrl, payload, params);

    check(res, { "Queue Token 생성 성공": (r) => r.status === 201 });

    const responseBody = res.json();
    const token = responseBody.data.tokenId;
    users.push({ userId: i, token });
  }

  console.log("✅ 모든 사용자의 토큰이 발급됨. 15초 대기 시작...");
  sleep(15); // 모든 토큰 발급 후 15초 대기

  return { users };
}

// ✅ 활성화된 토큰을 가진 500명의 사용자가 포인트 조회 & 충전 요청
export default function (data) {
  const users = data.users;
  const user = users[__VU % users.length]; 

  const headers = {
    "Queue_Token": `${user.token}`,
    "UserId": `${user.userId}`,
    "Content-Type": "application/json",
  };

  const chargeUrl = `http://localhost:8080/points/charge`;
  const chargePayload = JSON.stringify({
    userId: user.userId,
    chargePoint: 1000, 
  });

  const chargeRes = http.put(chargeUrl, chargePayload, { headers });

  check(chargeRes, { "포인트 충전 성공": (r) => r.status === 200 });

  sleep(1);
}

import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s", target: 200 },   // 5초 동안 200명 증가
    { duration: "5s", target: 500 },   // 5초 동안 500명 증가
    { duration: "10s", target: 1000 }, // 10초 동안 1000명 유지
    { duration: "5s", target: 500 },   // 5초 동안 500명 감소
    { duration: "5s", target: 0 },     // 5초 동안 0으로 감소
  ]
};

// ✅ 테스트 시작 전에 한 번만 실행
export function setup() {
  const queueTokenUrl = "http://localhost:8080/queue_tokens";

  const userId = 1; // Math.floor(Math.random() * 1000) + 1;
  
  const payload = JSON.stringify({ userId: userId });
  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(queueTokenUrl, payload, params);

  check(res, {
    "Queue Token 생성 성공": (r) => r.status === 201,
  });

  const responseBody = res.json(); 
  const token = responseBody.data.tokenId;  

  console.log(`✅ 생성된 토큰: ${token}`);

  return { token, userId }; // 전역 변수로 반환
}

// ✅ 여기서 받은 `token`과 `userId`를 사용
export default function (data) {
  const { token, userId } = data;

  const pointsUrl = `http://localhost:8080/points/${userId}`;
  const headers = {
    "Queue_Token": `${token}`,
    "UserId"     : `${userId}`,
    "Content-Type": "application/json",
  };

  const pointsRes = http.get(pointsUrl, { headers });

  check(pointsRes, {
    "잔여 포인트 조회 성공": (r) => r.status === 200,
  });

  sleep(1);
}

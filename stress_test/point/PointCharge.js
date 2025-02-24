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

  const userId = 1; 
  
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
  const token = responseBody?.data?.tokenId;  

  console.log(`✅ 생성된 토큰: ${token}`);

  if (!token) {
    console.error("❌ 토큰 생성 실패! 응답 확인 필요:", responseBody);
  }

  sleep(15); // ✅ 15초 대기 후 테스트 실행

  return { token, userId }; 
}

export default function (data) {
  const { token, userId } = data;

  if (!token) {
    console.error("❌ 토큰 없음! setup()에서 생성되지 않았을 가능성 있음.");
    return;
  }

  const payload = JSON.stringify({
    userId: userId,  
    chargePoint: 1
  });

  const headers = {
    "Queue_Token": token,  // ✅ Queue_Token이 정상적으로 들어갔는지 체크
    "UserId": userId.toString(), // 숫자를 문자열로 변환 (API에서 요구하는 형식이 다를 수 있음)
    "Content-Type": "application/json",
  };


  const params = { headers };

  const url = "http://localhost:8080/points/charge";
  const res = http.put(url, payload, params);

  check(res, { "is status 200": (r) => r.status === 200 });

  if (res.status !== 200) {
    console.error(`❌ 요청 실패! STATUS: ${res.status}, BODY: ${res.body}`);
  }

  sleep(1);
}

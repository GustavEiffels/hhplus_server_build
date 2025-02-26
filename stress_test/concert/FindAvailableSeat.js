import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s", target: 1000 },  
    { duration: "35s", target: 1000 }, 
    { duration: "5s", target: 400 },   
    { duration: "15s", target: 400 },  
  ],
};

export function setup() {
  const queueTokenUrl = "http://localhost:8080/queue_tokens";
  const users = [];

  for (let i = 1; i <= 1000; i++) {
    const payload = JSON.stringify({ userId: i });
    const params = { headers: { "Content-Type": "application/json" } };
    const res = http.post(queueTokenUrl, payload, params);

    check(res, { "Queue Token 생성 성공": (r) => r.status === 201 });

    const responseBody = res.json();
    const token = responseBody?.data?.tokenId;
    if (token) {
      users.push({ userId: i, token });
    }
  }

  console.log("✅ 모든 사용자 토큰 발급 완료. 15초 대기...");
  sleep(15); // 토큰 발급 후 15초 대기

  return { users };
}

export default function (data) {
  const users = data.users;
  const user = users[__VU % users.length]; // VU(가상 사용자)별로 user 매칭

  if (!user || !user.token) {
    console.error("❌ 유효한 사용자 또는 토큰이 없음!");
    return;
  }

  const schedule_id = 1;
  const url = `http://localhost:8080/concerts/${schedule_id}/available-seat`;
  const headers = {
    "Queue_Token": user.token,
    "UserId": user.userId.toString(),
    "Content-Type": "application/json",
  };

  const res = http.get(url, { headers });
  check(res, { "좌석 조회 성공": (r) => r.status === 200 });

  if (res.status !== 200) {
    console.error(`❌ 요청 실패! STATUS: ${res.status}, BODY: ${res.body}`);
  }

  sleep(1);
}

import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s", target: 1000 },  
    { duration: "10s", target: 1000 }, 
    { duration: "45s", target: 800 },  
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
  const user = users[__VU % users.length]; // 가상 사용자(VU)별로 user 매칭

  if (!user || !user.token) {
    console.error("❌ 유효한 사용자 또는 토큰이 없음!", user);
    return;
  }

  const token = user.token; // 토큰 변수 추가
  const concertScheduleId = Math.floor(Math.random() * 1000)+1; // 0 ~ 1000 사이 랜덤 값
  const seatId = concertScheduleId; // 콘서트 스케줄 아이디에 해당하는 좌석만 존재

  const payload = JSON.stringify({
    scheduleId: concertScheduleId,
    seatIdList: [seatId],
    userId: user.userId,
  });

  const headers = {
    "Queue_Token": token,               
    "UserId": user.userId.toString(),        
    "Content-Type": "application/json",
  };

  const url = `http://localhost:8080/reservations`;
  const res = http.post(url, payload, { headers });

  check(res, { "is status 200": (r) => r.status === 200 });

  if (res.status !== 200) {
    console.error(`❌ 요청 실패! User: ${user.userId}, STATUS: ${res.status}, BODY: ${res.body}`);
  }

  sleep(1);
}

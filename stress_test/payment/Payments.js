import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  setupTimeout: "120s",
  stages: [
    { duration: "10s", target: 500 }, 
    { duration: "10s", target: 400 }, 
    { duration: "10s", target: 100 }, 
  ],
};


// ✅ 테스트 시작 전에 한 번 실행 (대기열 토큰 발급 후 15초 대기 -> 예약 수행)
export function setup() {
  const queueTokenUrl = "http://localhost:8080/queue_tokens";
  const reservationUrl = "http://localhost:8080/reservations";
  const users = [];

  // 1️⃣ 사용자에게 대기열 토큰 발급
  for (let i = 1; i <= 1000; i++) {
    const payload = JSON.stringify({ userId: i });
    const params = { headers: { "Content-Type": "application/json" } };
    const res = http.post(queueTokenUrl, payload, params);

    check(res, { "Queue Token 생성 성공": (r) => r.status === 201 });

    if (res.status !== 201) {
      console.error(`❌ 토큰 발급 실패! User: ${i}, STATUS: ${res.status}`);
      continue;
    }

    const responseBody = res.json();
    const token = responseBody.data.tokenId;
    users.push({ userId: i, token });
  }

  console.log("✅ 모든 사용자의 토큰이 발급됨. 15초 대기 시작...");
  sleep(15); // 토큰 발급 후 15초 대기

  // 2️⃣ 모든 사용자 예약 진행
  for (let user of users) {
    const { userId, token } = user;
    const scheduleId = userId;
    const seatId = userId;
    const reservationPayload = JSON.stringify({
      scheduleId,
      seatIdList: [seatId],
      userId,
    });

    const reservationHeaders = {
      "Queue_Token": token,
      "UserId": userId.toString(),
      "Content-Type": "application/json",
    };

    const reservationRes = http.post(reservationUrl, reservationPayload, { headers: reservationHeaders });

    check(reservationRes, { "예약 성공": (r) => r.status === 200 });

    if (reservationRes.status !== 200) {
      console.error(`❌ 예약 실패! User: ${userId}, STATUS: ${reservationRes.status}`);
      continue;
    }
    user.reservationId = reservationRes.json().data.reservationInfoList[0].reservationId;
  }

  console.log("✅ 모든 사용자가 예약을 완료함. 결제 테스트 시작...");
  return { users };
}

// ✅ 예약된 정보를 기반으로 결제 요청 실행
export default function (data) {
  const users = data.users;
  const user = users[Math.floor(Math.random() * users.length)];

  if (!user || !user.token || !user.reservationId) {
    console.error("❌ 유효한 사용자/토큰/예약 정보 없음!", user);
    return;
  }

  const paymentPayload = JSON.stringify({
    reservationIds: [user.reservationId],
    userId: user.userId,
    tokenId: user.token,
  });

  const paymentHeaders = {
    "Queue_Token": user.token,
    "UserId": user.userId.toString(),
    "Content-Type": "application/json",
  };

  const paymentUrl = `http://localhost:8080/payments`;
  const paymentRes = http.post(paymentUrl, paymentPayload, { headers: paymentHeaders });

  check(paymentRes, { "결제 성공": (r) => r.status === 200 });
  
  sleep(1);
}

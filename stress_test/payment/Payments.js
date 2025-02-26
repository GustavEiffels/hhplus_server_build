import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s", target: 200 },   // 5초 동안 200명 증가
    { duration: "5s", target: 500 },   // 5초 동안 500명 증가
    { duration: "10s", target: 1000 }, // 10초 동안 1000명 유지 (부하 테스트)
    { duration: "5s", target: 500 },   // 5초 동안 500명 감소
    { duration: "5s", target: 0 },     // 5초 동안 0으로 감소
  ],
};

export function setup() {
  const queueTokenUrl = "http://localhost:8080/queue_tokens";
  const reservationUrl = "http://localhost:8080/reservations";

  let users = [];

  for (let i = 1; i <= 1000; i++) {
    // 1️⃣ 유저 토큰 생성
    const tokenPayload = JSON.stringify({ userId: i });
    const params = { headers: { "Content-Type": "application/json" } };
    
    const tokenRes = http.post(queueTokenUrl, tokenPayload, params);
    
    check(tokenRes, { "Queue Token 생성 성공": (r) => r.status === 201 });

    const tokenResponseBody = tokenRes.json();
    const token = tokenResponseBody?.data?.tokenId;

    if (!token) {
      console.error(`❌ User ${i} 토큰 생성 실패! 응답 확인 필요:`, tokenResponseBody);
      continue;
    }

    // 2️⃣ 좌석 예약 요청
    const reservationPayload = JSON.stringify({
      scheduleId: 1,      // 예매할 공연 스케줄 ID
      seatIdList: [i],    // 유저별로 좌석 ID를 다르게 설정
      userId: i
    });

    const reservationHeaders = {
      "Queue_Token": token,
      "UserId": i.toString(),
      "Content-Type": "application/json",
    };

    const reservationRes = http.post(reservationUrl, reservationPayload, { headers: reservationHeaders });

    check(reservationRes, { "예약 성공 (200)": (r) => r.status === 200 });

    if (reservationRes.status !== 200) {
      console.error(`❌ 예약 실패! User: ${i}, STATUS: ${reservationRes.status}, BODY: ${reservationRes.body}`);
      continue;
    }

    // 예약이 성공한 경우, 해당 유저 정보 저장
    users.push({ userId: i, token, reservationId: i });
  }

  sleep(15); // 15초 대기 후 결제 진행
  return { users };
}

export default function (data) {
  const users = data.users;
  const userIndex = Math.floor(Math.random() * users.length);
  const { userId, token, reservationId } = users[userIndex];

  if (!token) {
    console.error(`❌ User ${userId} 토큰 없음! setup()에서 생성되지 않았을 가능성 있음.`);
    return;
  }

  // 3️⃣ 결제 요청
  const paymentPayload = JSON.stringify({
    reservationIds: [reservationId],
    userId: userId,
    tokenId: token,
  });

  const paymentHeaders = {
    "Queue_Token": token,
    "UserId": userId.toString(),
    "Content-Type": "application/json",
  };

  const paymentUrl = `http://localhost:8080/payments`;
  const paymentRes = http.post(paymentUrl, paymentPayload, { headers: paymentHeaders });

  check(paymentRes, { "결제 성공 (201)": (r) => r.status === 201 });

  if (paymentRes.status !== 201) {
    console.error(`❌ 결제 실패! User: ${userId}, STATUS: ${paymentRes.status}, BODY: ${paymentRes.body}`);
  }

  sleep(1);
}

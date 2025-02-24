import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s", target: 200 },   // 5초 동안 200명 증가
    { duration: "5s", target: 500 },   // 5초 동안 500명 증가
    { duration: "10s", target: 1000 }, // 10초 동안 1000명 유지
    { duration: "5s", target: 500 },   // 5초 동안 500명 감소
    { duration: "5s", target: 0 },     // 5초 동안 0으로 감소
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"], // 95%의 요청이 500ms 이하인지 확인
  },
};

export default function () {
  const res = http.get("http://localhost:8080/points/1000");
  check(res, {"is status 200": (r) => r.status === 200,});
  sleep(1);
}

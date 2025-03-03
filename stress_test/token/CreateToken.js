import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  stages: [
    { duration: "5s",  target: 1000 }, 
    { duration: "15s", target: 1000 }, 
    { duration: "10s", target: 700 }, 
    { duration: "10s", target: 500 },  
    { duration: "10s", target: 100 },  
    { duration: "10s", target: 0 },    
  ],
};

export default function () {
  const url = "http://localhost:8080/queue_tokens";
  
  // 1 ~ 10000 범위에서 랜덤한 userId 선택
  const userId = Math.floor(Math.random() * (10000 - 1 + 1)) + 1;
  
  const payload = JSON.stringify({
    userId: userId, 
  });

  const params = {
    headers: { "Content-Type": "application/json" },
  };

  const res = http.post(url, payload, params);

  check(res, {
    "is status 201": (r) => r.status === 201,
  });

  sleep(1);
}
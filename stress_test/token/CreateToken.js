import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
    stages: [
      { duration: "10s", target: 200 },  
      { duration: "10s", target: 500 },  
      { duration: "20s", target: 1000 }, 
      { duration: "10s", target: 500 },  
      { duration: "10s", target: 0 },    
    ]
  };
  

export default function () {
  const url = "http://localhost:8080/queue_tokens";

  const payload = JSON.stringify({
    userId: 1, // Long 타입 userId 전달
  });

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    "is status 201": (r) => r.status === 201, 
  });

  sleep(1);
}

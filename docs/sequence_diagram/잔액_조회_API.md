```mermaid
---
title: 잔액 조회
---
sequenceDiagram
    Actor cl as Client
    participant us as UserService
    
    cl->> us : Request : 잔액 조회 요청
    us->> us : isExistUser : 존재하는 사용자인지 확인 

    alt 존재하는 사용자
        us-->>cl : Response ( "사용자의 포인트" )

    else 존재하지 않는 사용자
        us-->>cl : Error Response ("This User doesn't exist")
    end 
```
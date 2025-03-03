# 콘서트 예약 프로젝트 요구사항 분석 
> 콘서트 예약 로직을 구현을 위한 요구 사항 분석
> 1. 유저 대기열 토큰 기능 
> 2. 예약 가능 날짜/ 좌석 API
> 3. 좌석 예약 요청 API
> 4. 잔액 충전 / 충전 조회 
> 5. 결제 API 

## 유저 대기열 토큰 기능 
1. 대기열은 왜 필요한가?
   > 트래픽량을 조절하여 안정적인 운영을 하기 위해서 

2. 필요한 기능 
   > 1. 클라이언트가 대기열 요청하면 토큰 생성 ( 이때 상태 값은  wait ) 으로 설정 
   > 2. scheduler 를 이용하여 일정 량의 사람 수 만큼 state: active 로 설정
   >> - active scheduler : 대기열의 토큰을 active 로 변경 
   >> - expire scheduler : 만료시간이 지난 토큰을 delete 로 변경 
   >> ( 여기서 soft delete hard delete 고민하기 )

3. 정리
   > 클라이언트가 대기열에 진입하기 위한 토큰을 발급 받음 
   request  : userid 
   response : tokenid 
   > - scheduler 가 돌면서 토큰의 상태값을 변경 시킴 




## 예약 가능 날짜
1. 필요한 기능 
    > 1. 대기열에 들어와야 함으로 -> 대기열에 있는 토큰인지 확인 
    > 2. 콘서트 아이디로 남은 티켓수가 0 이 아닌 콘서트 디테일의 정보를 반환
    > ( 이때 현재 시간이 예약 가능 시간인 콘서트만 반환 ) 
2. 정리 
    > 대기열에 포함된 클라이언트가, 예약 가능한 콘서트 디테일 정보를 리스트로 반환 받음 

request  : 
> userid ( token 과 user 검증 )
> tokenid ( 대기열 토큰인지 ), 
> concertId ( 콘서트 아이디 )
    
response :
> concert-title                  : 콘서트 이름 
> concert-performer              : 공연자 
> concert-price                  : 공연 가격 
> consertdetail-time             : 콘서트 시간 정보 
> consertdetail-reservationStart : 예약 시작 시간 
> consertdetail-reservationEnd   : 예약 종료 시간  
> consertdetail-leftTicketCnt    : 남은 티켓 개수




## 예약 가능 좌석 API 
1. 필요한 기능 
   > 1. 대기열에 존재하는 클라이언트인지 확인 
   > 2. 예약 가능한 콘서트인지 확인 ( concertDetail-leftTicketCnt 가 > 0 )
   > 3. 예약이 가능한 좌석 list 반환 
   > (  Seat-status : reservable  => seat 는 미리 생성되어 있어야함   )
2. 정리
    > 대기열에 포함된 클라이언트가, 예약 가능한 좌석정보를 리스트로 받음 

request:
> userid 
> tokenid 
> concertDetailId 

response: 
> list : 예약 가능한 좌석 번호 리스트 




## 좌석 예약 요청 API
1. 필요한 기능 
> 1. 대기열에 존재하는 클라이언트인지 확인 
> 2. 콘서트아이디, 날짜, 좌석 번호를 입력받고 락을 건다
> 3. 좌석 레코드의 상태값을 변경한다 ( Seat-status : occupied )
> 4. 예약 레코드에 값을 생성한다.

2. 요약
> 대기열에 포함된 클라이언트가, 콘서트 아이디, 날짜, 좌석 값을 입력하여 
> 접근 가능할 경우 예약을 완료 시킨다.

request:
> userid
> tokenid
> concertid
> concertDate
> seatNumber

response: 
> seat-number
> concert-title
> concert-showTime
> concert-price


## 잔액 충전 
1. 필요한 기능 
   > 1. userid 를 통해 존재하는 사용자인지 확인 
   > 2. 충전 금액을 입력받아서 금액 충전하기 -> 낙관적 lock 사용하기 ( 충돌이 적음 )
2. 정리
request: 
> userid, charge

response:
> user-point (충전 결과)


## 금액 조회
1. 필요한 기능 
    > 1. userId -> 존재하는 사용자인지 확인 
    > 2. 현재 금액 확인 

2. 정리

request:
> userid 

response:
> user-point 

## 결제 
1. 필요한 기능 
    > 1. 대기열에 존재하는 클라이언트인지 확인 
    > 2. 예약정보 조회 
    > 3. 결제
        -> 좌석 점유 시간 초과  
            => reservation status cancel 
            => seat        status reservable 
        -> 좌석 점유 시간 이내 
            => 포인트 조회 lock 을 검
            => 예약 정보 update
            => 사용자의 포인트 차감 
    
2. 결제 시 
    > 존재하는 예약을 결제하려고 하는 token 시간이 만료 시간전에 결제 시
    최종 예약 결제 완료

request:
> userid
> tokeid
> reservationId

response:
> resservation-status
> user-point


## 테이블 

## 초기 버전 
### User
- id
- name
- point
- createAt
- updateAt

### Seat
- id
- concertDetail
- status ( default : reserved, reservable )
- number 
- price 
- expireAt
- createAt
- updateAt

### Reservation
- id
- userid
- concertScheduleId
- status ( default : reserved, cancel, purchase)
- createAt
- updateAt

### QueueToken
- id
- userId
- status : 토큰의 상태 값 ( default : wait, active, delete)
- expireAt : 토큰 만료 시간 -> 토큰의 status 가 active 되고 나서 설정 
- createAt 
- updateAt

### Concert
- id
- title
- performer
  
### ConcertSchedule
- id
- concertId
- showTime  : 공연 시작 시간 ( 2024-11-11 11:00 )
- reservationStart : 
- reservationEnd   : 
- leftTicket       

## 수정된 버전 : 2025-01-01
```mermaid
---
title: Concert API
---
erDiagram
    User {
        int id PK           
        string name         "사용자 이름 "
        int point           "잔여 포인트"
        datetime createAt   "생성 시간"
        datetime updateAt   "최근 수정 시간"
    }
    Seat {
        int id PK
        int scheduleId    FK    "[콘서트 스케줄] 테이블  외래키" 
        string status           "좌석 상태 : reserved (default), occupied,reservable"
        int number              "좌석 번호"
        decimal price           "좌석 가격"
        datetime expiredAt      "점유 만료 시간"
        datetime createAt       "생성 시간"
        datetime updateAt       "최근 수정 시간"
    }
    Reservation {
        int id PK               
        int userId FK           "[사용자] 테이블 외래키"           
        int seatId FK           "[좌석] 테이블 외래키"
        string status           "예약 상태 : reserved (default), cancel, purchase"
        datetime createAt       "생성 시간"
        datetime updateAt       "최근 수정 시간"
    }
    QueueToken {
        int id PK               
        int userId FK       "[사용자] 테이블 외래키" 
        string status       "대기열 토큰 상태 : wait (default), active, delete"
        datetime expireAt   "대기열 토큰 만료 시간"
        datetime createAt   "생성 시간"
        datetime updateAt   "최근 수정 시간"
    }

    Concert {
        int id PK   
        string title        "콘서트 이름"
        string performer    "콘서트 공연자 이름"
    }

    ConcertSchedule {
        int id PK
        int concertId FK            "[콘서트] 테이블 외래키"
        datetime showTime           "공연 시작 시간"
        datetime reservationStart   "예약 시작 시간"
        datetime reservationEnd     "예약 종료 시간"
        int leftTicket              "남은 티켓 수"
        datetime createAt           "생성 시간"
        datetime updateAt           "최근 수정 시간"

    }

    User ||--o{ Reservation : "User:Reservation = 1:N"
    User ||--o{ QueueToken  : "User:QueueToken = 1:N"
    Reservation ||--|| Seat : "Reservation:Seat = 1:1"
    Concert ||--o{ ConcertSchedule : "Concert:ConcertSchedule = 1:1"
    ConcertSchedule ||--o{ Seat : "ConcertSchedule:Seat = 1:1"
        
```
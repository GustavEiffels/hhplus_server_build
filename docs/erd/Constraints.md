### Seat
> - 좌석 가격은 10_000 에서 100_000 까지 구성하기 
> - 좌석 번호는 1 - 50 까지만 구성 


### Resrvation
> - 상태는 기본적으로 "reserved", 예약이 취소 되면 "cancel" 예약이 확정 된 것은 "purchase" 


### QueueToken
> 제약 조건은 아니고 QueueToken Active 하는 스케줄만 작성하려고 함 

### Payment 
> quantity 는 0 보다 반드시 커야한다 

### ConcertSchedule 
> Show Time 은 현재 보다 과거 여야 한다 
> reservationStart < reservationEnd 필수 

### User
> point 의 최소 값은 0 이며 최대 값은 100_000_000 이다. 

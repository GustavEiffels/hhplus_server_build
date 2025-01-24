# 동시성 이슈 파악 및 제어방식 도입 및 장단점 파악 

## 1. 들어가면서
```
콘서트 예약을 주제로 하였고, 
동시성 이슈가 
1.포인트 충전 
2.좌석 조회
3.결제

위 3가지 경우에 발생할 것으로 파악하여
보고서를 작성하였습니다.
```

## 2. 포인트 충전
**포인트 충전 로직 개요**
```
포인트 충전로직 
1. 사용자를 조회한다. ( 사용자 테이블 안에 사용자의 포인트 필드가 존재한다. ) 
2. 사용자의 포인트를 충전 한다.
3. 충전 내역을 생성하고 저장한다.
```
**동시성이 일어날 것이라고 생각한 이유**
```
이 로직에서 동시성 문제가 발생할 수 있는 부분은 사용자 조회 단계입니다. 
여러 스레드가 동시에 사용자의 정보를 조회할 수 있으며, 각 스레드는 조회 후 포인트를 수정하는 작업을 진행하게 됩니다.
이때 두 스레드가 동일한 사용자의 포인트 정보를 동시에 조회한 후, 서로 다른 값으로 포인트를 업데이트하려고 할 수 있습니다.
이로 인해 포인트 값이 덮어쓰여지는 경쟁 조건 문제가 발생할 수 있습니다.
```

[COMMIT]([05e93f0036bfdbf8edcd32a7765a2474a65f4f9c](https://github.com/GustavEiffels/hhplus_server_build/pull/34/commits/05e93f0036bfdbf8edcd32a7765a2474a65f4f9c))
1. 낙관적 락 사용 테스트 결과

**thread10**
![alt text](image.png)
```
15:55:35.022 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Error Message : org.springframework.orm.ObjectOptimisticLockingFailureException : Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [kr.hhplus.be.server.domain.user.User#1]
15:55:35.022 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - 충전 금액 : 10000
15:55:35.022 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Thread 개수 : 10 | duration : 137 ms
```
**thread50**
![alt text](image.png)
```
15:54:43.003 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Error Message : org.springframework.orm.ObjectOptimisticLockingFailureException : Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [kr.hhplus.be.server.domain.user.User#1]
15:54:43.003 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - 충전 금액 : 10000
15:54:43.003 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Thread 개수 : 50 | duration : 252 ms
```
**thread200**
![alt text](image.png)
```
5:53:17.217 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Error Message : org.springframework.orm.ObjectOptimisticLockingFailureException : Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [kr.hhplus.be.server.domain.user.User#1]
15:53:17.217 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Error Message : org.springframework.dao.DataAccessResourceFailureException : unable to obtain isolated JDBC connection [HangHaePlusDataSource - Connection is not available, request timed out after 10008ms (total=60, active=60, idle=0, waiting=1)] [n/a]
15:53:17.217 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Error Message : org.springframework.transaction.CannotCreateTransactionException : Could not open JPA EntityManager for transaction
15:53:17.217 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - 충전 금액 : 20000
15:53:17.217 [Test worker] INFO  k.h.b.s.l.o.PointConcurrencyTest - Thread 개수 : 200 | duration : 10364 ms
```
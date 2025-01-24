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


**1. 낙관적 락 사용 테스트 결과**  [COMMIT]([05e93f0036bfdbf8edcd32a7765a2474a65f4f9c](https://github.com/GustavEiffels/hhplus_server_build/pull/34/commits/05e93f0036bfdbf8edcd32a7765a2474a65f4f9c))
**2. 비관적 락 사용 테스트 결과**  [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/d1f05b17ecf222169ad8cf99a561e8a151361af8)
**3. 분산 락 사용 테스트 결과**   [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/8d865221966ec5cf8c7044447720270b4cbc40f2)


## 3. 좌석 예약
**포인트 충전 로직 개요**
```
좌석 예약 로직
1. 예약된 좌석인지 확인 
2. 예약 되지 않은 좌석인 경우 - 상태 : 예약으로 변경,
3. 해당 공연 스케줄의 좌석이 모두 매진 된 경우 - 상태 : 예약 불가능 으로 변경
3. 예약 생성 
```
**동시성이 일어날 것이라고 생각한 이유**
```
다수의 사용자들이 
좌석을 예약하기 위해 특정 좌석에서 
경합이 많이 일어날 것이라고 판단 하였습니다.
```
**1. 낙관적 락 사용 테스트 결과**  [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/b26ba49bc9a591553e6ecfa67eab827659484e0a)
**2. 비관적 락 사용 테스트 결과**  [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/803b0a900af8d81bff4cc679e7683b5da7d00a32)
**3. 분산 락 사용 테스트 결과**   [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/00973d159b8fcf5c58d70e592a6045c010787d95)


## 4. 결제
**결제 로직 개요**
```
결제 로직 
1. 결제할 예약들 조회 
2. 결제할 사람들 조회 
3. 사용자의 포인트와 총 결제 금액을 비교 
4. 결제 가능 시 결제 생성  
5. 결제 성공 시 결제 내역이 생성  
```
**동시성이 일어날 것이라고 생각한 이유**
```
결제할 예약들을 조회하는 작업은 사용자가 직접 진행하므로 경합이 발생할 가능성은 적지만, 
스케줄에서 만료된 예약을 조회하여 좌석 상태를 변경하기 때문에 현재 비관적 락을 사용하고 있습니다.
```
**1. 낙관적 락 사용 테스트 결과**  [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/f55c71e41a1edd627b0d57d195b86e30a3ba2424)
**2. 비관적 락 사용 테스트 결과**  [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/db62068ecfea62467bae711d14b1c07a3df35c2f)
**3. 분산 락 사용 테스트 결과**   [COMMIT](https://github.com/GustavEiffels/hhplus_server_build/commit/b044e1763379b4b3d9842c8315410d1d5aa49899)


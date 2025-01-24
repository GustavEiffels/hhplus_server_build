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
**검증 테스트 코드**
```java
    private  void pointChargeTest(int threadCnt) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(threadCnt);

        List<Boolean> results      = new ArrayList<>(threadCnt);
        HashSet<String>  errorMessage = new HashSet<>(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                try {
                    startSignal.await();
                    PointFacadeDto.ChargeParam param = new PointFacadeDto.ChargeParam(newUser.getId(), 10_000L);
                    facade.pointCharge(param);
                    results.add(true);
                } catch (Exception e) {
                    errorMessage.add(e.getClass().getName()+" : "+e.getLocalizedMessage());
                    results.add(false);
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();
        doneSignal.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long successCnt = results.stream().filter(Boolean::booleanValue).count();

        User updatedUser = userJpaRepository.findById(newUser.getId()).orElseThrow();
        assertEquals(10000*successCnt, updatedUser.getPoint());

        pointHistoryJpaRepository.findAll().forEach(item->{
            assertEquals(PointHistoryStatus.CHARGE,item.getStatus());
            assertEquals(10_000L,item.getAmount());
        });

        for(String message : errorMessage){ log.info("Error Message : {}",message);}
        log.info("충전 금액 : {}",updatedUser.getPoint());
        log.info("Thread 개수 : {} | duration : {} ms",threadCnt,(endTime-startTime));
    }

    @DisplayName("사용자가 동시에 포인트를 충전하지 않을 때, 충전 후 사용자 포인트이 충전 시도한 금액과 같아야한다. ( 사용자 포인트 = 충전 시도 금액 1 + 충전시도 금액 2+ ...")
    @Test
    void notConcurrencyTest(){
        // given
        long startTime = System.currentTimeMillis();
        int tryCnt = 10;

        // when
        for(int i = 0; i <tryCnt; i++){
            PointFacadeDto.ChargeParam param = new PointFacadeDto.ChargeParam(newUser.getId(), 10_000L);
            facade.pointCharge(param);
        }

        // then
        User findUser = userJpaRepository.findById(newUser.getId()).orElseThrow();
        assertEquals(10000*tryCnt,findUser.getPoint());
        long endTime = System.currentTimeMillis();
        log.info(" duration : {} ms",(endTime-startTime));
    }

```
1. 낙관적 락 사용

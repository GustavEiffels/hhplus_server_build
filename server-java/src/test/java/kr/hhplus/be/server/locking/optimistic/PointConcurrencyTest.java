package kr.hhplus.be.server.locking.optimistic;

import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.domain.point_history.PointHistoryStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.point_history.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;



@Slf4j
@SpringBootTest
@Testcontainers
@DisplayName("""
            포인트 충전 - 낙관적 락을 사용     
            """)
//@ActiveProfiles("optimistic")
public class PointConcurrencyTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    PointFacade facade;

    @BeforeEach
    void setUp(){
        User user = User.create("김연습");
        newUser  = userJpaRepository.save(user);
    }

    User newUser;


    @DisplayName("10개의 Thread 가 동시에 충전 요청 시, 충전 성공 횟수 * 충전 금액 은 사용자의 포인트와 같다. ( 충전 성공 횟수 * 충전 금액 = 사용자의 포인트 )")
    @Test
    void concurrency_thread_10() throws InterruptedException {
        pointChargeTest(10);
    }

    @DisplayName("50개의 Thread 가 동시에 충전 요청 시, 충전 성공 횟수 * 충전 금액 은 사용자의 포인트와 같다. ( 충전 성공 횟수 * 충전 금액 = 사용자의 포인트 )")
    @Test
    void concurrency_thread_50() throws InterruptedException{
        pointChargeTest(50);
    }

    @DisplayName("200개의 Thread 가 동시에 충전 요청 시, 충전 성공 횟수 * 충전 금액 은 사용자의 포인트와 같다. ( 충전 성공 횟수 * 충전 금액 = 사용자의 포인트 )")
    @Test
    void concurrency_thread_200() throws InterruptedException{
        pointChargeTest(200);
    }

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

}

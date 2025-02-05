package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.domain.point_history.PointHistory;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("포인트 충전 통합 테스트")
public class PointConcurrencyTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    PointFacade facade;

    User newUser;

    @DisplayName("""
                 * 포인트가 0 원인 사용자가 충전을 하려고 한다
                 * 충전할 금액은 10,000 이다.
                 *
                 * 서버상의 실수로 50번 정도 충전 요청을 하더라도
                 * 사용자의 포인트는 10,000 가 되어야함
            """)
    @Test
    void concurrencyTest() throws InterruptedException {
        int numThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(numThreads); // 모든 스레드가 작업 완료 시점 확인

        List<Boolean> results = new ArrayList<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                try {
                    startSignal.await(); // 모든 스레드가 동시에 시작
                    PointFacadeDto.ChargeParam param = new PointFacadeDto.ChargeParam(newUser.getId(), 10_000L);
                    facade.pointCharge(param);
                    results.add(true);
                } catch (Exception e) {
                    results.add(false);
                } finally {
                    doneSignal.countDown(); // 작업 완료 신호
                }
            });
        }

        startSignal.countDown(); // 모든 스레드 작업 시작
        doneSignal.await(); // 모든 스레드 작업 완료 대기
        executorService.shutdown();

        // 성공한 충전 요청 개수 확인 (1번만 성공해야 함)
        long successCnt = results.stream().filter(Boolean::booleanValue).count();
        assertEquals(1, successCnt);

        // 사용자의 최종 포인트 확인 (10,000이어야 함)
        User updatedUser = userJpaRepository.findById(newUser.getId()).orElseThrow();
        assertEquals(10_000L, updatedUser.getPoint());

        pointHistoryJpaRepository.findAll().forEach(item->{
            assertEquals(PointHistoryStatus.CHARGE,item.getStatus());
            assertEquals(10_000L,item.getAmount());
        });
    }


    @BeforeEach
    void setUp(){
        User user = User.create("김연습");
        newUser = userJpaRepository.save(user);
    }

}

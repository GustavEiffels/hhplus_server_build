package kr.hhplus.be.server.compare.queue;


import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            대기열 - 기존 : DB 테이블 사용       
            """)
@ActiveProfiles("local")
public class QueueDBConcurrencyTest {
    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    TokenJpaRepository tokenJpaRepository;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    User owner;
    @BeforeEach
    void setUp(){
      owner = userJpaRepository.save(User.create("test"));
    }

    // CREATE TOKEN TEST

    @DisplayName("""
            DB 를 사용하여 대기열 토큰을 생성하는 테스트 :
            토큰 생성 시, 토큰 생성이 성공한 수만큼 대기열 영역에 토큰 수가 존재한다.
            """)
    @Test
    void createQueueToken() throws InterruptedException {
        int threadNum  = 150;
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal  = new CountDownLatch(threadNum);
        AtomicInteger count = new AtomicInteger();

        for(int i = 0; i<threadNum; i++){
            executorService.submit(()->{
                try {
                    startSignal.await();
                    count.incrementAndGet();
                    // given&when : 토큰 생성
                    QueueTokenFacadeDto.CreateParam param = new QueueTokenFacadeDto.CreateParam(owner.getId());
                    queueTokenFacade.create(param);
                } catch (Exception e) {
                    log.error("Error during task execution", e);
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        startSignal.countDown();
        doneSignal.await();
        executorService.shutdown();
        long endTime = System.currentTimeMillis();


        Assertions.assertEquals(count.get(),tokenJpaRepository.count(),"토큰 생성 시, 토큰 생성이 성공한 수만큼 대기열 영역에 토큰 수가 존재한다.");
        log.info("DB : CREATE TOKEN CNT : "+tokenJpaRepository.count());
        log.info("success cnt : {}",count.get());
        log.info("during : {} ms ",(endTime - startTime));
    }


}

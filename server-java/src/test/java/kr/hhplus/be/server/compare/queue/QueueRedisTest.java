package kr.hhplus.be.server.compare.queue;


import kr.hhplus.be.server.application.queue_token.QueueTokenFacade;
import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.queue_token.TokenRedisRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
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
            대기열 : REDIS 사용        
            """)
@ActiveProfiles("redis")
public class QueueRedisTest {

    @Autowired
    TokenRedisRepository redisRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;


    User owner;
    @BeforeEach
    void setUp(){
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        owner = userJpaRepository.save(User.create("test"));
    }

    @DisplayName("""
            Redis 를 사용하여 대기열 토큰을 생성하는 테스트 - 동시성 :
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


        Assertions.assertEquals(count.get(),redisRepository.countWaiting(),"토큰 생성 시, 토큰 생성이 성공한 수만큼 대기열 영역에 토큰 수가 존재한다.");
        log.info("REDIS : CREATE TOKEN CNT : "+redisRepository.countWaiting());
        log.info("success cnt : {}",count.get());
        log.info("during : {} ms ",(endTime - startTime));
    }

    @DisplayName("""
            REDIS 를 사용하여 토큰 활성화 - Active = 100
            """)
    @Test
    void activateToken_100(){
        // given
        for(int i = 0; i<1000; i++){
            QueueTokenFacadeDto.CreateParam param = new QueueTokenFacadeDto.CreateParam(owner.getId());
            queueTokenFacade.create(param);
        }
        long startTime = System.currentTimeMillis();

        //when
        queueTokenFacade.activate(new QueueTokenFacadeDto.ActivateParam(100L));

        long endTime = System.currentTimeMillis();
        log.info("REDIS - 활성화된 토큰 수 : "+redisRepository.countActive());
        log.info("during : {} ms ",(endTime - startTime));
    }
    @DisplayName("""
            REDIS 를 사용하여 토큰 활성화 - Active = 100
            """)
    @Test
    void activateToken_1000(){
        // given
        for(int i = 0; i<3000; i++){
            QueueTokenFacadeDto.CreateParam param = new QueueTokenFacadeDto.CreateParam(owner.getId());
            queueTokenFacade.create(param);
        }
        long startTime = System.currentTimeMillis();

        //when
        queueTokenFacade.activate(new QueueTokenFacadeDto.ActivateParam(1000L));

        long endTime = System.currentTimeMillis();
        log.info("REDIS - 활성화된 토큰 수 : "+redisRepository.countActive());
        log.info("during : {} ms ",(endTime - startTime));
    }
}

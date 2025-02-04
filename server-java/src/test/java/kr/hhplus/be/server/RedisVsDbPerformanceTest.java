package kr.hhplus.be.server;


import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            Redis 와 DB 간의 간략한 조회 쓰기 비교 테스트 
            """)
public class RedisVsDbPerformanceTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    RedissonClient redissonClient;



    @Transactional
    void saveUser(String name){
        userJpaRepository.save(User.create(name));
    }

    public void addString(String key, String value){
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }


    @DisplayName("""
            STRING 사용 - 단순 쓰기 성능 비교 테스트 : 쓰기 100 회가 완료되는 시간을 측정하여 비교합니다. 
            """)
    @Test
    void redis_strings_test_00() throws InterruptedException {
        int threadNum = 100;
        concurrencyTester(i -> saveUser("test"+i), threadNum,"데이터 베이스");
        concurrencyTester(i -> addString(i+"","test"+i),threadNum,"Redis String");
    }


    private void concurrencyTester(Consumer<Integer> task, int threadNum, String type) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal  = new CountDownLatch(threadNum);

        // success cnt
        AtomicInteger count = new AtomicInteger();

        for(int i = 0; i<threadNum; i++){
            int finalI = i;
            executorService.submit(()->{
                try {
                    startSignal.await();
                    task.accept(finalI);
                    count.incrementAndGet();
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
        log.info("---------------------------------------");
        log.info("type   : {}",type);
        log.info("success cnt : {}",count.get());
        log.info("during : {} ms ",(endTime - startTime));
        log.info("---------------------------------------");
    }


}

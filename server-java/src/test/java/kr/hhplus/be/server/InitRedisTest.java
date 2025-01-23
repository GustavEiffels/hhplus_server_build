package kr.hhplus.be.server;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
@Slf4j
public class InitRedisTest {



    @Test
    void test(){
        RedissonClient redissonClient = Redisson.create();

        RBucket<String> bucket = redissonClient.getBucket("testKey");
        bucket.set("testValue");

        String value = bucket.get();
        Assertions.assertEquals("testValue",value);
        log.info("Redis 로 부터 설정한 값 TEST : {}",bucket.get());
        redissonClient.shutdown();

    }
}

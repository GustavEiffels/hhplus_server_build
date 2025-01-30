package kr.hhplus.be.server;


import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointFacadeDto;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
@Slf4j
@ActiveProfiles("optimistic")
public class InitRedisTest {

    @DisplayName("Redis 연결 테스트")
    @Test
    void redis_init_connection_test(){
        RedissonClient redissonClient = Redisson.create();

        RBucket<String> bucket = redissonClient.getBucket("testKey");
        bucket.set("testValue");

        String value = bucket.get();
        Assertions.assertEquals("testValue",value);
        log.info("Redis 로 부터 설정한 값 TEST : {}",bucket.get());
        redissonClient.shutdown();
    }

    @Autowired
    PointFacade pointFacade;

    @Autowired
    UserJpaRepository userJpaRepository;

    User newUser;

    @BeforeEach
    void setUp(){
        User user = User.create("test");
        newUser = userJpaRepository.save(user);
    }
    @DisplayName("lockTest")
    @Test
    void redis_lock_test(){

        PointFacadeDto.ChargeParam chargeParam = new PointFacadeDto.ChargeParam(newUser.getId(), 100L);
        pointFacade.pointCharge(chargeParam);
    }

}

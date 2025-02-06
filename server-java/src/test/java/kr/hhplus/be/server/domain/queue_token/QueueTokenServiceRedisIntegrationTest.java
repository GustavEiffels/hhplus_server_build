package kr.hhplus.be.server.domain.queue_token;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            대기열 - redis 사용       
            """)
@ActiveProfiles("redis")
class QueueTokenServiceRedisIntegrationTest {
    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    QueueTokenService queueTokenService;

    @Autowired
    QueueTokenRepository repository;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    User owner;
    @BeforeEach
    void setUp(){
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        owner = userJpaRepository.save(User.create("test"));
    }

    @DisplayName("""
            REDIS 사용 - 대기열에 토큰이 30개 활성화 된 토큰이 0 일때, 최대 활성화 가능 토큰 개수가 20개면
            활성화 영역에 20개의 토큰이 추가가 되고, 대기영역에 10개의 토큰만 존재한다.
            """)
    @Test
    void activate_00(){
        //given
        for(int i = 0; i<30; i++){
            queueTokenService.createToken(owner.getId());
        }

        // when
        queueTokenService.activate(20L);

        // then
        assertEquals(20,repository.countActiveTokens());
        assertEquals(10,repository.countWaiting());
    }

}
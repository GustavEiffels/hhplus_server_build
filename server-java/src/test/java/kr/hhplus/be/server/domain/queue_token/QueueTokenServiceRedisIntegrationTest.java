package kr.hhplus.be.server.domain.queue_token;

import kr.hhplus.be.server.application.queue_token.QueueTokenFacadeDto;
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
            REDIS 사용 - 대기열에 토큰이 40개 활성화 된 토큰이 0 일때, 최대 활성화 가능 토큰 개수가 30개면
            활성화 영역에 30개의 토큰이 추가가 되고, 대기영역에 10개의 토큰만 존재한다.
            """)
    @Test
    void activate_00(){
        //given
        for(int i = 0; i<40; i++){
            queueTokenService.createToken(owner.getId());
        }

        // when
        queueTokenService.activate(30L);

        // then
        assertEquals(30,repository.countActiveTokens());
        assertEquals(10,repository.countWaiting());
    }

    @DisplayName("""
            REDIS 사용 - 활성화 시간이 3초로 설정되어 있고, 100개의 대기열 토큰 중,
            30개가 활성화 되고 3초가 지난 후, 만료된 토큰의 개수는 30개이며,
            토큰을 삭제시키는 로직을 수행하면 활성화된 토큰의 개수는 0이다.
            """)
    @Test
    void expire_00() throws InterruptedException {
        // given
        for(int i =0; i<100; i++){
            queueTokenService.createToken(owner.getId());
        }
        queueTokenService.activate(30L);
        Thread.sleep(3000);

        // when & then
        assertEquals(30,repository.findExpiredFromActive(System.currentTimeMillis()).size(), "30개가 활성화 되고 3초가 지난 후, 만료된 토큰의 개수는 30개 이다.");

        // when
        queueTokenService.expireToken();

        //then
        assertEquals(0L,repository.countActive(),"토큰을 삭제시키는 로직을 수행하면 활성화된 토큰의 개수는 0이다.");


    }

}
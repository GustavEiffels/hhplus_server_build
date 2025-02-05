package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.queue_token.QueueTokenRepository;
import kr.hhplus.be.server.domain.queue_token.QueueTokenService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.queue_token.TokenRedisRepository;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
@Slf4j
@DisplayName("""
            대기열 - redis 사용       
            """)
@ActiveProfiles("redis")
class QueueTokenFacadeRedisTest {
    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    QueueTokenRepository queueTokenRepository;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    @Autowired
    QueueTokenService queueTokenService;

    User owner;

    User other;

    @BeforeEach
    void setUp(){
        owner = userJpaRepository.save(User.create("test1"));
        other = userJpaRepository.save(User.create("test2"));
    }

    @DisplayName("owner 가 토큰을 생성하면 redis 에 userId 와 매핑 되며, 대기열에 포함 되어 있다.")
    @Test
    void create_TEST(){
        // given & when
        QueueTokenFacadeDto.CreateParam param = new QueueTokenFacadeDto.CreateParam(owner.getId());
        QueueTokenFacadeDto.CreateResult result = queueTokenFacade.create(param);

        // then
        String queueToken = result.tokenId();

        assertNotNull(queueTokenRepository.findWaitingTokenByTokenId(queueToken),"대기열 토큰에 존재한다.");
        assertEquals(owner.getId(),queueTokenRepository.findUserIdByTokenId(queueToken),"대기열 토큰의 소유자는 OWNER 이다.");
        assertFalse(queueTokenService.isValidAndActive(owner.getId(),queueToken),"대기열에 존재하는 토큰임으로 false");
    }

}
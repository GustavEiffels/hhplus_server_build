package kr.hhplus.be.server.application.queue_token;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    TokenJpaRepository tokenJpaRepository;

    @Autowired
    QueueTokenFacade queueTokenFacade;

    List<User> userList;

    @BeforeEach
    void setUp(){
        List<User> newUserList = userList = new ArrayList<>();
        for(int i = 0; i<10; i++){
            newUserList.add(User.create("바보"+i));
        }
        userList = userJpaRepository.saveAll(newUserList);
    }

}
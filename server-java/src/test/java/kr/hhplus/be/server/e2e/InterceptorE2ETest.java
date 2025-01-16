package kr.hhplus.be.server.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queue_token.QueueToken;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.queue_token.TokenJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
public class InterceptorE2ETest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    TokenJpaRepository tokenJpaRepository;

    MockMvc mockMvc;

    User testUser;

    QueueToken queueToken;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 사용자 생성
        testUser = User.create("TestUser");
        testUser.pointTransaction(50_000L);
        testUser = userJpaRepository.save(testUser);
    }

    @DisplayName("""
            대기열 토큰이 활성화 된 경우,
            잔여 포인트 조회 시, 
            잔여 포인트를 조회한다. 
            """)
    @Test
    void interceptorE2E_Test00() throws Exception {
        // 활성화 된 토큰
        queueToken = QueueToken.create(testUser);
        queueToken.activate();
        queueToken = tokenJpaRepository.save(queueToken);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/points/{userId}", testUser.getId())
                                .header("Queue_Token", queueToken.getId())
                                .header("UserId", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("\"balance\":50000"));

    }

    @DisplayName("""
            대기열 토큰이 활성화 되지 않는 경우
            현재 대기열 대기 중이라는 메세지를 받게 된다.
            """)
    @Test
    void interceptorE2E_Test01() throws Exception {
        // 활성화 되지 않은 토큰
        queueToken = QueueToken.create(testUser);
        queueToken = tokenJpaRepository.save(queueToken);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/points/{userId}", testUser.getId())
                                .header("Queue_Token", queueToken.getId())
                                .header("UserId", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();


        String content = result.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("현재 대기열 대기 중 입니다."));
    }

}

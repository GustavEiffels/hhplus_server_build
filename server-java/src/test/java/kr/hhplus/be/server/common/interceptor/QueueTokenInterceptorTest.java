package kr.hhplus.be.server.common.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class QueueTokenInterceptorTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void test(){
        webTestClient.get().uri("/concerts")
                .attribute("Queue_Token",1)
                .attribute("UserId",1)
                .exchange();
    }



}
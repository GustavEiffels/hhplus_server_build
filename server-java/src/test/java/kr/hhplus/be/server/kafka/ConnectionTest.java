package kr.hhplus.be.server.kafka;

import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import kr.hhplus.be.server.interfaces.consumer.KafkaPlatformConsumer;
import kr.hhplus.be.server.infrastructure.reservation.ReservationProducer;
import kr.hhplus.be.server.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@Testcontainers
public class ConnectionTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final CountDownLatch latch = new CountDownLatch(1);
    private String receivedMessage;

    @KafkaListener(topics = "my-first-topic", groupId = "my-consumer-group")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received Message: {}", record.value());
        this.receivedMessage = record.value();
        latch.countDown();
    }

    public void produce(String topic, Object message) {
        try {
            log.info("Message from producer: {}", message);
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage);
        } catch (Exception e) {
            log.error("Exception: {}", e.getLocalizedMessage());
            throw new BusinessException(ErrorCode.JSON_PARSING_EXCEPTION);
        }
    }

    @DisplayName("Kafka Producer로 User를 전송하고 Consumer에서 같은 값을 수신한다.")
    @Test
    void sendToKafka_00() throws InterruptedException, JsonProcessingException {
        // given
        User user = User.create("TEST STRING");

        // when
        produce("my-first-topic", user);

        latch.await(15, TimeUnit.SECONDS);

        // then
        assertEquals(
                new ObjectMapper().writeValueAsString(user),
                this.receivedMessage,
                "Kafka Producer 로 User 를 전송하면, Consumer 로 반환 받은 값과 User 를 JSON 직렬화 한 값과 같다.");
    }
}

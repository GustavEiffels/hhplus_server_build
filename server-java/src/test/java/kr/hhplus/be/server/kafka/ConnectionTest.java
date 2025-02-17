package kr.hhplus.be.server.kafka;

import kr.hhplus.be.server.interfaces.controller.reservation.KafkaPlatformConsumer;
import kr.hhplus.be.server.infrastructure.reservation.ReservationProducer;
import kr.hhplus.be.server.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableKafka
@Slf4j
public class ConnectionTest {

    @Autowired
    ReservationProducer reservationProducer;

    @Autowired
    KafkaPlatformConsumer kafkaConsumer;

    @DisplayName("Kafka 에서 메세지 보내면, 해당 메세지가 전송이 된다.")
    @Test
    void sendToKafka_00() throws InterruptedException, JsonProcessingException {
        // given
        User user = User.create("TEST STRING");

        // when
        reservationProducer.sendReservation("my-first-topic", user);

        kafkaConsumer.awaitLatch();
        String receivedMessage = kafkaConsumer.getReceivedMessage();

        assertEquals(new ObjectMapper().writeValueAsString(user),receivedMessage);
    }
}

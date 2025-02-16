package kr.hhplus.be.server.kafka;

import kr.hhplus.be.server.common.config.kafka.KafkaPlatformConsumer;
import kr.hhplus.be.server.common.config.kafka.ReservationProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void sendToKafka_00() throws InterruptedException {
        // given
        HashMap<String, String> exerciseMap = new HashMap<>();
        exerciseMap.put("name", "kim");
        exerciseMap.put("age", "16");

        // when
        reservationProducer.sendReservation("my-first-topic", exerciseMap);

        boolean isMessageReceived = kafkaConsumer.awaitLatch();

        String receivedMessage = kafkaConsumer.getReceivedMessage();
        assertEquals("{name=kim, age=16}", receivedMessage);
    }
}

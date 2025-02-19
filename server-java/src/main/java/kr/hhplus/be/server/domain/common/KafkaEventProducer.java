package kr.hhplus.be.server.domain.common;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class KafkaEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private void produce(String topic, String message) {
        kafkaTemplate.send(topic, message).get();  // 동기 전송 (에러 캐치 가능)
        log.info("Message sent to Kafka topic {}: {}", topic, message);
    }

}

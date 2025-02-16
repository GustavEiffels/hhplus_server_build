package kr.hhplus.be.server.common.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendReservation(String topic, Object message){
        kafkaTemplate.send(topic,message);
    }
}

package kr.hhplus.be.server.infrastructure.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exceptions.BusinessException;
import kr.hhplus.be.server.common.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendReservation(String topic, Object message){
        try {
            log.info("message from producer : {}",message);
            String jsonMessage = new ObjectMapper().writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage);
        } catch (Exception e) {
            log.info("EXCEPTION : {}",e.getLocalizedMessage());
            throw new BusinessException(ErrorCode.JSON_PARSING_EXCEPTION);
        }

    }
}

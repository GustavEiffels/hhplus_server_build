package kr.hhplus.be.server.application.event;

import kr.hhplus.be.server.domain.common.KafkaEventProducer;
import kr.hhplus.be.server.domain.event.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.platform.ReservationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final KafkaEventProducer kafkaEventProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationSuccessHandler(ReservationSuccessEvent event){
        try {
            OutBox eventOutBox = event.getOutBox();
            kafkaEventProducer.produce(eventOutBox.getEventType(),eventOutBox.getPayload());
            log.info("Send to ReservationClient Success!");
        }
        catch (Exception e){
            log.info("Send to ReservationClient fail : {}",e.getLocalizedMessage());
        }
    }
}

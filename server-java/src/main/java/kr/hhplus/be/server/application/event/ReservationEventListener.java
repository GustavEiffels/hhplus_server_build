package kr.hhplus.be.server.application.event;

import kr.hhplus.be.server.common.config.Json.JsonUtils;
import kr.hhplus.be.server.domain.common.KafkaEventProducer;
import kr.hhplus.be.server.domain.event.reservation.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final KafkaEventProducer kafkaEventProducer;
    private final OutBoxService outBoxService;
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveReservationCreateEvent (ReservationSuccessEvent event){
        outBoxService.create(event.getOutBox());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationSuccessHandler(ReservationSuccessEvent event){
        try {
            OutBox outBox = event.getOutBox();
            kafkaEventProducer.produce(outBox.getEventType(), outBox.getId() ,outBox.getPayload());
            log.info("Send to ReservationClient Success!");
        }
        catch (Exception e){
            log.info("Send to ReservationClient fail : {}",e.getLocalizedMessage());
        }
    }
}

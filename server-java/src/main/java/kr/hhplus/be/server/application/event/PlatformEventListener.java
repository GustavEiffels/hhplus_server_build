package kr.hhplus.be.server.application.event;


import kr.hhplus.be.server.domain.common.KafkaEventProducer;
import kr.hhplus.be.server.domain.event.dataplatform.PlatformOutBoxUpdateEvent;
import kr.hhplus.be.server.domain.outbox.OutBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatformEventListener {

    private final KafkaEventProducer kafkaEventProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void platformOutBoxUpdateHandler(PlatformOutBoxUpdateEvent event){
        OutBox outBox = event.getOutBox();
        kafkaEventProducer.produce("reservation_outbox_update",outBox.getPayload());
    }
}

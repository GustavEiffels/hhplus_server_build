package kr.hhplus.be.server.interfaces.consumer;


import kr.hhplus.be.server.domain.outbox.OutBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationOutBoxUpdateConsumer {

    private final OutBoxService outBoxService;

    private CountDownLatch latch = new CountDownLatch(1);

    public boolean awaitLatch() throws InterruptedException {
        return latch.await(5, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = "reservation_outbox_update",groupId = "reservation")
    public void listenReservationOutboxUpdate(ConsumerRecord<String,String> record){
        latch.countDown();
        log.info("Received Message - reservation_outbox_update  : {}",record.value());
        outBoxService.updateFromConsumer(record.value());
    }
}
